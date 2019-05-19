package service.others;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Movie;
import model.others.CustomerWithLoyaltyCard;
import model.others.CustomerWithMoviesAndSalesStand;
import model.others.MovieWithSalesStand;
import model.tickets_data_filtering.MovieFilteringCriterion;
import repository.others.JoinedEntitiesRepository;
import utils.others.EmailUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JoinedEntitiesService {

  private final JoinedEntitiesRepository joinedEntitiesRepository;

  public Map<Movie, Integer> movieGroupedByPopularity() {

    return joinedEntitiesRepository.getMovieWithSalesStand()
            .stream()
            .collect(Collectors.groupingBy(JoinedEntitiesService::convertMovieWithSalesStandToMovie, Collectors.summingInt(e -> 1)));
  }

  public Set<Movie> allMoviesBoughtSortedAlphabetically() {
    return joinedEntitiesRepository.getMovieWithSalesStand()
            .stream().map(JoinedEntitiesService::convertMovieWithSalesStandToMovie)
            .sorted(Comparator.comparing(Movie::getTitle))
            .collect(Collectors.toCollection(LinkedHashSet::new));
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

    return joinedEntitiesRepository.getCustomerWithMoviesAndSalesStand()
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

/*  public List<CustomerWithMoviesAndSalesStand> allTicketsTransactionHistory(Integer id) {

    var allTicketsByCustomerId = joinedEntitiesRepository.getAllTicketsByCustomerId(id);

    EmailUtils.sendAllSummaryTable(joinedEntitiesRepository.getCustomerEmailByCustomerId(id), "Movie summary", allTicketsByCustomerId);
    return allTicketsByCustomerId;
  }*/

  public Set<CustomerWithMoviesAndSalesStand> getCustomerMoviesByFilters(Integer customerId, Map<MovieFilteringCriterion, List<? extends Object>> movieFilters) {

    return joinedEntitiesRepository.getAllTicketsByCustomerId(customerId)
            .stream()
            .filter(customerWithMoviesAndSalesStand -> getMovieFilterPredicate(movieFilters).test(customerWithMoviesAndSalesStand))
            .collect(Collectors.toSet());
  }
//    EmailUtils.sendSummaryTableByFilters("firelight.code@gmail.com"/*salesStandRepository.getCustomerEmailByCustomerId(id)*/, "From app", allFilteredTickets, filters);


  private static Predicate<CustomerWithMoviesAndSalesStand> getMovieFilterPredicate
          (Map<MovieFilteringCriterion, List<?>> filters) {
    return filters
            .entrySet()
            .stream()
            .map(JoinedEntitiesService::getPredicate)
            .reduce(Predicate::and).orElseThrow(() -> new AppException(""));
  }

  private static Predicate<CustomerWithMoviesAndSalesStand> getPredicate
          (Map.Entry<MovieFilteringCriterion, List<?>> cus) {
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

  public List<CustomerWithMoviesAndSalesStand> getMoviesDetailsByCustomerId(Integer id) {
    return joinedEntitiesRepository.getAllTicketsByCustomerId(id);
  }

  public Integer ticketsNumberBoughtByCustomerId(Integer customerId) {
    return joinedEntitiesRepository.getAllTicketsByCustomerId(customerId).size();
  }

  public List<CustomerWithLoyaltyCard> getCustomersWithLoyaltyCardWithActiveLoyaltyCard() {
    return joinedEntitiesRepository.getAllCustomerWithLoyaltyCard().stream()
            .filter(obj -> obj.getMoviesNumber() > 0 && obj.getLoyaltyCardExpirationDate().compareTo(LocalDate.now()) >= 0)
            .collect(Collectors.toList());
  }

  private boolean doCustomerPosesActiveLoyaltyCardByCustomerId(Integer customerId) {
    var customerWithLoyaltyCardOptional = joinedEntitiesRepository.getCustomerWithLoyaltyCardInfoByCustomerId(customerId);

    return customerWithLoyaltyCardOptional.isPresent() &&
            customerWithLoyaltyCardOptional.get().getMoviesNumber() > 0 &&
            customerWithLoyaltyCardOptional.get().getLoyaltyCardExpirationDate().compareTo(LocalDate.now()) > 0;
  }

//    public void manageLoyaltyCard(Customer customer, Integer ticketsNumber, Movie movie, LocalDateTime movieStartTime) {
//
//    if (!doCustomerPosesActiveLoyaltyCardByCustomerId(customer.getId())) {
//      verifyIfCustomerCanGetLoyaltyCard(ticketsNumber, customer);//dorobic to aby  resetowac ilosc ticketow po założeniu loyaltyCard tak aby przy następnym założeniu była brana aktualna liczba ticketow
//    } else {
//      var loyaltyCardId = joinedEntitiesRepository.getCustomerWithLoyaltyCardInfoByCustomerId(customer.getId()).get().getLoyaltyCardId();
//      decreaseMoviesNumberByLoyaltyCardId(loyaltyCardId);
//      movie.setPrice(movie.getPrice().subtract(joinedEntitiesRepository.findById(loyaltyCardId).get().getDiscount()));
//    }
//
//    sendMovieDetailsToCustomerEmail(customer.getEmail(), "Movie Ticket purchase detail", movie, movieStartTime);
//  }
//
//  private void verifyIfCustomerCanGetLoyaltyCard(Integer ticketsNumber, Customer customer) {
//
//    /*odczyt z pliku jesli nie ma id customera !=-1 to wez ticketsNumber z argumentu metody, w przeciwnym razie wez ticketsNumber - wartosc odczytana z pliku*/
//    ticketsNumber = readTicketsNumberFromFileByCustomerId(customer) == -1 ? ticketsNumber : ticketsNumber - readTicketsNumberFromFileByCustomerId(customer);
//
//    if (ticketsNumber >= LOYALTY_CARD_MIN_MOVIE_NUMBER) {
//      switch (UserDataUtils.getString("Do you want to add a loyalty card? (y/n)").toLowerCase()) {
//        case "y" -> {
//          addLoyaltyCardForCustomer(customer);
//          //zapis do pliku aktualnej wartosci ticketsNumber dla klienta
//          addOrUpdateTicketsNumberToFileBoughtByCustomer(ticketsNumber, customer);
//        }
//        case "n" -> System.out.println("TOO BAD. MAYBE NEXT TIME!");
//        default -> verifyIfCustomerCanGetLoyaltyCard(ticketsNumber, customer);/*throw new AppException("ACTION NOT DEFINED");*/
//      }
//    }
//  }

  private void sendMovieDetailsToCustomerEmail(String email, String subject, Movie movie, LocalDateTime startDateTime) {
    EmailUtils.sendMoviePurchaseConfirmation(email, subject, movie, startDateTime);
  }
}
