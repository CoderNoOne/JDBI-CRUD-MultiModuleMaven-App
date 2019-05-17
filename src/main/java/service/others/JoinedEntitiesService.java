package service.others;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Customer;
import model.entity.Movie;
import model.others.CustomerWithMoviesAndSalesStand;
import model.others.MovieWithSalesStand;
import model.tickets_data_filtering.MovieFilteringCriterion;
import repository.entity_repository.impl.SalesStandRepository;
import repository.others.JoinedEntitiesRepository;
import service.entity_service.SalesStandService;
import utils.EmailUtils;
import utils.TicketsFilteringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

  public List<CustomerWithMoviesAndSalesStand> allTicketsTransactionHistory(Integer id) {

    var allTicketsByCustomerId = joinedEntitiesRepository.getAllTicketsByCustomerId(id);

    EmailUtils.sendAllSummaryTable("firelight.code@gmail.com"/*salesStandRepository.getCustomerEmailByCustomerId(id)*/, "Movie summary", allTicketsByCustomerId);
    return allTicketsByCustomerId;
  }

  public List<CustomerWithMoviesAndSalesStand> filterTicketsTransactionHistory(Integer id) {

    var filters = TicketsFilteringUtils.getMovieFilters("Specify your movie filters\n").getFilters();

    var allFilteredTickets = joinedEntitiesRepository.getAllTicketsByCustomerId(id)
            .stream()
            .filter(customerWithMoviesAndSalesStand -> getMovieFilterPredicate(filters).test(customerWithMoviesAndSalesStand))
            .collect(Collectors.toList());

    EmailUtils.sendSummaryTableByFilters("firelight.code@gmail.com"/*salesStandRepository.getCustomerEmailByCustomerId(id)*/, "From app", allFilteredTickets, filters);
    return allFilteredTickets;
  }

  private static Predicate<CustomerWithMoviesAndSalesStand> getMovieFilterPredicate(Map<MovieFilteringCriterion, List<?>> filters) {
    return filters
            .entrySet()
            .stream()
            .map(JoinedEntitiesService::getPredicate)
            .reduce(Predicate::and).orElseThrow(() -> new AppException(""));
  }

  private static Predicate<CustomerWithMoviesAndSalesStand> getPredicate(Map.Entry<MovieFilteringCriterion, List<?>> cus) {
    Predicate<CustomerWithMoviesAndSalesStand> predicate;
    switch (cus.getKey()) {
      case MOVIE_DURATION -> predicate = filterByMovieDurationPredicate(cus);
      case MOVIE_RELEASE_DATE -> predicate = filterByMovieReleaseDate(cus);
      case MOVIE_GENRE -> predicate = filterByMoviesGenre(cus);
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

//  public boolean isTransactionDone(Movie movie, Customer customer, LocalDateTime startDateTime) {
//    if (!addSalesStand(movie, customer, startDateTime)) {
//      throw new AppException("Movie start date time is not valid");
//    }
//    return true /*ticketsNumberBoughtByCustomerId(customer.getId())*/;
//  }
}
