package service.entity_service;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.others.CustomerWithMoviesAndSalesStand;
import model.tickets_data_filtering.MovieFilteringCriterion;
import repository.impl.SalesStandRepository;
import utils.EmailUtils;
import utils.TicketsFilteringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class SalesStandService {

  private final SalesStandRepository salesStandRepository;

  public List<CustomerWithMoviesAndSalesStand> allTicketsTransactionHistory(Integer id) {

    var allTicketsByCustomerId = salesStandRepository.getAllTicketsByCustomerId(id);


    EmailUtils.sendSummaryTable("firelight.code@gmail.com", "Movie summary", allTicketsByCustomerId);
    return allTicketsByCustomerId;
  }

  public List<CustomerWithMoviesAndSalesStand> filterTicketsTransactionHistory(Integer id) {


    var allFilteredTickets = salesStandRepository.getAllTicketsByCustomerId(id)
            .stream()
            .filter(SalesStandService::getMovieFilterPredicate)
            .collect(Collectors.toList());


    //wysyłanie maila

    EmailUtils.sendSummaryTable("firelight.code@gmail.com","From app", allFilteredTickets);
    return allFilteredTickets;
  }

  private static boolean getMovieFilterPredicate(CustomerWithMoviesAndSalesStand customerWith) {
    return TicketsFilteringUtils.getMovieFilters("Specify your movie filters\n").getFilters()
            .entrySet()
            .stream()
            .map(SalesStandService::getPredicate)
            .reduce(Predicate::and).orElseThrow(() -> new AppException("")).test(customerWith);
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

}

