package service.others;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Customer;
import model.entity.Movie;
import model.others.CustomerWithLoyaltyCard;
import model.others.CustomerWithMoviesAndSalesStand;
import model.tickets_data_filtering.MovieFilteringCriterion;
import repository.others.JoinedEntitiesRepository;
import utils.entity.JoinedEntitiesUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JoinedEntitiesService {

  private final JoinedEntitiesRepository joinedEntitiesRepository;

  public Map<Movie, Integer> movieGroupedByPopularity() {

    return joinedEntitiesRepository.getAllMovieWithSalesStand()
            .stream()
            .collect(Collectors.groupingBy(JoinedEntitiesUtils::convertMovieWithSalesStandToMovie, Collectors.summingInt(e -> 1)));
  }

  public Set<Movie> allDistinctMoviesBoughtSortedAlphabetically() {
    return joinedEntitiesRepository.getAllMovieWithSalesStand()
            .stream().map(JoinedEntitiesUtils::convertMovieWithSalesStandToMovie)
            .sorted(Comparator.comparing(Movie::getTitle))
            .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public List<CustomerWithMoviesAndSalesStand> allMoviesBoughtByCustomer(Integer customerId) {
    return new ArrayList<>(joinedEntitiesRepository.getAllTicketsByCustomerId(customerId));
  }

  public Set<Movie> allDistinctMoviesBoughtBySpecifiedCustomerSortedAlphabetically(Integer customerId) {
    return joinedEntitiesRepository.getAllTicketsByCustomerId(customerId)
            .stream()
            .map(JoinedEntitiesUtils::convertCustomerWithMoviesAndSalesStandsToMovie)
            .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Movie::getTitle))));
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
                    Collectors.mapping(JoinedEntitiesUtils::convertCustomerWithMoviesAndSalesStandsToCustomer,
                            Collectors.filtering(customer -> customer.getAge() >= minAge && customer.getAge() <= maxAge, Collectors.toList()))));
  }

  public Set<CustomerWithMoviesAndSalesStand> getCustomerMoviesByFilters(Integer customerId, Map<MovieFilteringCriterion, List<? extends Object>> movieFilters) {

    return joinedEntitiesRepository.getAllTicketsByCustomerId(customerId)
            .stream()
            .filter(customerWithMoviesAndSalesStand -> JoinedEntitiesUtils.getMovieFilterPredicate(movieFilters).test(customerWithMoviesAndSalesStand))
            .collect(Collectors.toSet());
  }

  public List<CustomerWithLoyaltyCard> getCustomersWithActiveLoyaltyCard() {
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

  public List<Movie> allMoviesBoughtBySpecifiedCustomer(Customer customer) {
    return joinedEntitiesRepository.getAllTicketsByCustomerId(customer.getId())
            .stream()
            .map(JoinedEntitiesUtils::convertCustomerWithMoviesAndSalesStandsToMovie)
            .collect(Collectors.toList());
  }

  public Integer numberOfMoviesBoughtByCustomer(Customer customer) {
    return allMoviesBoughtBySpecifiedCustomer(customer).size();
  }


  public Map<String, Double> getAverageCustomerAgeWhichBoughtAtLeastSpecifiedNumberOfTicketGroupedForEachMovieGenre(int minMovieNumber) {

    if (minMovieNumber <= 0) {
      throw new AppException("Movie number should be greater than 0!");
    }

    return joinedEntitiesRepository.getAllCustomerWithMoviesAndSalesStand()
            .stream()
            .collect(Collectors.groupingBy(obj -> JoinedEntitiesUtils.convertCustomerWithMoviesAndSalesStandsToMovie(obj).getGenre(),
                    Collectors.groupingBy(JoinedEntitiesUtils::convertCustomerWithMoviesAndSalesStandsToCustomer, Collectors.counting())))
            .entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().entrySet().stream().filter(innerEntry -> innerEntry.getValue() >= minMovieNumber).collect(Collectors.averagingInt(innerEntry -> innerEntry.getKey().getAge()))));
  }
}
