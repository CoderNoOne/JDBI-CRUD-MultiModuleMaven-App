package service.entity_service;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Customer;
import model.entity.Movie;
import model.entity.SalesStand;
import model.others.CustomerWithMoviesAndSalesStand;
import model.tickets_data_filtering.MovieFilteringCriterion;
import repository.entity_repository.impl.SalesStandRepository;

import utils.EmailUtils;
import utils.TicketsFilteringUtils;
import validators.impl.SalesStandValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class SalesStandService {

  private final SalesStandRepository salesStandRepository;

  public List<CustomerWithMoviesAndSalesStand> allTicketsTransactionHistory(Integer id) {

    var allTicketsByCustomerId = salesStandRepository.getAllTicketsByCustomerId(id);

    EmailUtils.sendAllSummaryTable("firelight.code@gmail.com"/*salesStandRepository.getCustomerEmailByCustomerId(id)*/, "Movie summary", allTicketsByCustomerId);
    return allTicketsByCustomerId;
  }

  public List<CustomerWithMoviesAndSalesStand> filterTicketsTransactionHistory(Integer id) {

    var filters = TicketsFilteringUtils.getMovieFilters("Specify your movie filters\n").getFilters();

    var allFilteredTickets = salesStandRepository.getAllTicketsByCustomerId(id)
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
            .map(SalesStandService::getPredicate)
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


  private static SalesStand createSalesStand(Integer movieId, Integer customerId, LocalDateTime startDateTime) {
    return SalesStand.builder()
            .customerId(customerId)
            .movieId(movieId)
            .startDateTime(startDateTime)
            .build();
  }

  public boolean addSalesStand(Movie movie, Customer customer, LocalDateTime startDateTime) {
    var salesStand = createSalesStand(movie.getId(), customer.getId(), startDateTime);
    boolean isValid = new SalesStandValidator().validateEntity(salesStand);

    if (isValid) {
      salesStandRepository.add(salesStand);
    }
    return isValid;
  }

  public List<CustomerWithMoviesAndSalesStand> getMoviesDetailsByCustomerId(Integer id) {
    return salesStandRepository.getAllTicketsByCustomerId(id);
  }

  public Integer ticketsNumberBoughtByCustomerId(Integer customerId) {
    return salesStandRepository.getAllTicketsByCustomerId(customerId).size();
  }

  public Integer buyTicket(Movie movie, Customer customer, LocalDateTime startDateTime) {
    if (!addSalesStand(movie, customer, startDateTime)) {
      throw new AppException("Movie start date time is not valid");
    }
    return ticketsNumberBoughtByCustomerId(customer.getId());
  }
}

