package service.others;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Customer;
import model.entity.Movie;
import model.others.CustomerWithLoyaltyCard;
import model.others.CustomerWithMoviesAndSalesStand;
import model.others.MovieWithSalesStand;
import model.tickets_data_filtering.MovieFilteringCriterion;
import repository.others.JoinedEntitiesRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JoinedEntitiesService {

  private final JoinedEntitiesRepository joinedEntitiesRepository;

  public Map<Movie, Integer> movieGroupedByPopularity() {

    return joinedEntitiesRepository.getAllMovieWithSalesStand()
            .stream()
            .collect(Collectors.groupingBy(JoinedEntitiesService::convertMovieWithSalesStandToMovie, Collectors.summingInt(e -> 1)));
  }

  public Set<Movie> allDistinctMoviesBoughtSortedAlphabetically() {
    return joinedEntitiesRepository.getAllMovieWithSalesStand()
            .stream().map(JoinedEntitiesService::convertMovieWithSalesStandToMovie)
            .sorted(Comparator.comparing(Movie::getTitle))
            .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public List<CustomerWithMoviesAndSalesStand> allMoviesBoughtByCustomer(Integer customerId) {
    return new ArrayList<>(joinedEntitiesRepository.getAllTicketsByCustomerId(customerId));
  }

  public Set<Movie> allDistinctMoviesBoughtBySpecifiedCustomerSortedAlphabetically(Integer customerId) {
    return joinedEntitiesRepository.getAllTicketsByCustomerId(customerId)
            .stream()
            .map(JoinedEntitiesService::convertCustomerWithMoviesAndSalesStandsToMovie)
            .collect(Collectors.toCollection(TreeSet::new));
  }

  private static Movie convertCustomerWithMoviesAndSalesStandsToMovie(CustomerWithMoviesAndSalesStand customerWithMoviesAndSalesStand) {
    return Movie.builder()
            .title(customerWithMoviesAndSalesStand.getMovieTitle())
            .genre(customerWithMoviesAndSalesStand.getMovieGenre())
            .price(customerWithMoviesAndSalesStand.getTicketPrice())
            .duration(customerWithMoviesAndSalesStand.getMovieDuration())
            .releaseDate(customerWithMoviesAndSalesStand.getMovieReleaseDate())
            .build();
  }

  public Map<Integer, Map<String, Integer>> mostPopularMovieGenreForEachCustomer() {

    return joinedEntitiesRepository.getAllCustomerWithMoviesAndSalesStand()
            .stream()
            .collect(Collectors.groupingBy(CustomerWithMoviesAndSalesStand::getCustomerId,
                    Collectors.mapping(CustomerWithMoviesAndSalesStand::getMovieGenre,
                            Collectors.groupingBy(Function.identity(),
                                    Collectors.summingInt(e -> 1)))))
            .entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().entrySet().stream()
                            .filter(innerMap -> innerMap.getValue() >= e.getValue().entrySet().stream().max(Map.Entry.comparingByValue()).get().getValue())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
  }

  public Map<String, List<Customer>> customersWhoBoughtMoviesWithCategoryAndWithAgeWithinRange(int minAge, int maxAge) {

    return joinedEntitiesRepository.getAllCustomerWithMoviesAndSalesStand()
            .stream()
            .collect(Collectors.groupingBy(CustomerWithMoviesAndSalesStand::getMovieGenre,
                    Collectors.mapping(JoinedEntitiesService::convertCustomerWithMoviesAndSalesStandsToCustomer,
                            Collectors.filtering(customer -> customer.getAge() >= minAge && customer.getAge() <= maxAge, Collectors.toList()))));
  }

  private static Customer convertCustomerWithMoviesAndSalesStandsToCustomer(CustomerWithMoviesAndSalesStand customerWithMoviesAndSalesStand){
    return Customer.builder()
            .id(customerWithMoviesAndSalesStand.getCustomerId())
            .name(customerWithMoviesAndSalesStand.getCustomerName())
            .surname(customerWithMoviesAndSalesStand.getCustomerSurname())
            .age(customerWithMoviesAndSalesStand.getCustomerAge())
            .email(customerWithMoviesAndSalesStand.getCustomerEmail())
            .loyaltyCardId(customerWithMoviesAndSalesStand.getCustomerLoyaltyCardId())
            .build();
  }

  private static Movie convertMovieWithSalesStandToMovie(MovieWithSalesStand movieWithSalesStand) {
    return Movie.builder()
            .id(movieWithSalesStand.getMovieId())
            .title(movieWithSalesStand.getMovieTitle())
            .genre(movieWithSalesStand.getMovieGenre())
            .price(movieWithSalesStand.getMoviePrice())
            .duration(movieWithSalesStand.getMovieDuration())
            .releaseDate(movieWithSalesStand.getMovieReleaseDate())
            .build();
  }

  public Set<CustomerWithMoviesAndSalesStand> getCustomerMoviesByFilters(Integer customerId, Map<MovieFilteringCriterion, List<? extends Object>> movieFilters) {

    return joinedEntitiesRepository.getAllTicketsByCustomerId(customerId)
            .stream()
            .filter(customerWithMoviesAndSalesStand -> getMovieFilterPredicate(movieFilters).test(customerWithMoviesAndSalesStand))
            .collect(Collectors.toSet());
  }


  private static Predicate<CustomerWithMoviesAndSalesStand> getMovieFilterPredicate
          (Map<MovieFilteringCriterion, List<?>> filters) {
    return filters
            .entrySet()
            .stream()
            .map(JoinedEntitiesService::getPredicate)
            .reduce(Predicate::and).orElseThrow(() -> new AppException(""));
  }

  private static Predicate<CustomerWithMoviesAndSalesStand> getPredicate(Map.Entry<MovieFilteringCriterion, List<?>> cus) {
    Predicate<CustomerWithMoviesAndSalesStand> predicate;
    switch (cus.getKey()) {
      case DURATION -> predicate = filterByMovieDurationPredicate(cus);
      case RELEASE_DATE -> predicate = filterByMovieReleaseDate(cus);
      case GENRE -> predicate = filterByMoviesGenre(cus);
      default -> throw new AppException("");
    }
    return predicate;
  }

  private static Predicate<CustomerWithMoviesAndSalesStand> filterByMoviesGenre(Map.Entry<MovieFilteringCriterion, List<?>> cus) {
    return customerWithMoviesAndSalesStand -> cus.getValue().stream().map(String::valueOf).
            anyMatch(genre -> customerWithMoviesAndSalesStand.getMovieGenre().equalsIgnoreCase(genre));
  }

  private static Predicate<CustomerWithMoviesAndSalesStand> filterByMovieReleaseDate(Map.Entry<MovieFilteringCriterion, List<?>> cus) {

    var minLocalDate = LocalDate.parse(String.valueOf(cus.getValue().get(0)));
    var maxLocalDate = LocalDate.parse(String.valueOf(cus.getValue().get(1)));
    return customerWithMoviesAndSalesStand ->
            customerWithMoviesAndSalesStand.getMovieReleaseDate().compareTo(minLocalDate) >= 0 &&
                    customerWithMoviesAndSalesStand.getMovieReleaseDate().compareTo(maxLocalDate) <= 0;
  }

  private static Predicate<CustomerWithMoviesAndSalesStand> filterByMovieDurationPredicate(Map.Entry<MovieFilteringCriterion, List<?>> cus) {

    return customerWithMoviesAndSalesStand ->
            customerWithMoviesAndSalesStand.getMovieDuration() >= Integer.parseInt(String.valueOf(cus.getValue().get(0)))
                    && customerWithMoviesAndSalesStand.getMovieDuration() <= Integer.parseInt(String.valueOf(cus.getValue().get(1)));
  }

  public List<CustomerWithLoyaltyCard> getCustomersWithLoyaltyCardWithActiveLoyaltyCard() {
    return joinedEntitiesRepository.getAllCustomerWithLoyaltyCard().stream()
            .filter(obj -> obj.getMoviesNumber() > 0 && obj.getLoyaltyCardExpirationDate().compareTo(LocalDate.now()) >= 0)
            .collect(Collectors.toList());
  }

  public boolean doCustomerPosesActiveLoyaltyCard(Customer customer) {
    var customerWithLoyaltyCardOptional = joinedEntitiesRepository.getCustomerWithLoyaltyCardInfoByCustomerId(customer.getId());

    return customerWithLoyaltyCardOptional.isPresent() &&
            customerWithLoyaltyCardOptional.get().getMoviesNumber() > 0 &&
            customerWithLoyaltyCardOptional.get().getLoyaltyCardExpirationDate().compareTo(LocalDate.now()) > 0;
  }

  public Optional<CustomerWithLoyaltyCard> getCustomerWithLoyaltyCardByCustomer(Customer customer) {

    return joinedEntitiesRepository.getCustomerWithLoyaltyCardInfoByCustomerId(customer.getId());
  }

  private List<Movie> allMoviesBoughtBySpecifiedCustomer(Customer customer) {
    return joinedEntitiesRepository.getAllTicketsByCustomerId(customer.getId())
            .stream()
            .map(JoinedEntitiesService::convertCustomerWithMoviesAndSalesStandsToMovie)
            .collect(Collectors.toList());
  }

  public Integer numberOfMoviesBoughtByCustomer(Customer customer) {
    return allMoviesBoughtBySpecifiedCustomer(customer).size();
  }
}
