package entity;

import exceptions.AppException;
import entity.Customer;
import entity.Movie;
import others.CustomerWithMoviesAndSalesStand;
import others.MovieWithSalesStand;
import tickets_data_filtering.MovieFilteringCriterion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface JoinedEntitiesUtils {

  static Customer convertCustomerWithMoviesAndSalesStandsToCustomer(CustomerWithMoviesAndSalesStand customerWithMoviesAndSalesStand) {
    return Customer.builder()
            .id(customerWithMoviesAndSalesStand.getCustomerId())
            .name(customerWithMoviesAndSalesStand.getCustomerName())
            .surname(customerWithMoviesAndSalesStand.getCustomerSurname())
            .age(customerWithMoviesAndSalesStand.getCustomerAge())
            .email(customerWithMoviesAndSalesStand.getCustomerEmail())
            .loyaltyCardId(customerWithMoviesAndSalesStand.getCustomerLoyaltyCardId())
            .build();
  }

  static Movie convertMovieWithSalesStandToMovie(MovieWithSalesStand movieWithSalesStand) {
    return Movie.builder()
            .id(movieWithSalesStand.getMovieId())
            .title(movieWithSalesStand.getMovieTitle())
            .genre(movieWithSalesStand.getMovieGenre())
            .price(movieWithSalesStand.getMoviePrice())
            .duration(movieWithSalesStand.getMovieDuration())
            .releaseDate(movieWithSalesStand.getMovieReleaseDate())
            .build();
  }

  static CustomerWithMoviesAndSalesStand convertMovieToCustomerWithMoviesAndSalesStands(Movie movie, LocalDateTime movieStartTime) {
    return CustomerWithMoviesAndSalesStand.builder()
            .movieTitle(movie.getTitle())
            .movieReleaseDate(movie.getReleaseDate())
            .ticketPrice(movie.getPrice())
            .movieGenre(movie.getGenre())
            .startDateTime(movieStartTime)
            .build();
  }

  static Movie convertCustomerWithMoviesAndSalesStandsToMovie(CustomerWithMoviesAndSalesStand customerWithMoviesAndSalesStand) {
    return Movie.builder()
            .title(customerWithMoviesAndSalesStand.getMovieTitle())
            .genre(customerWithMoviesAndSalesStand.getMovieGenre())
            .price(customerWithMoviesAndSalesStand.getTicketPrice())
            .duration(customerWithMoviesAndSalesStand.getMovieDuration())
            .releaseDate(customerWithMoviesAndSalesStand.getMovieReleaseDate())
            .build();
  }

  static Predicate<CustomerWithMoviesAndSalesStand> getMovieFilterPredicate(Map<MovieFilteringCriterion, List<?>> filters) {
    return filters
            .entrySet()
            .stream()
            .map(JoinedEntitiesUtils::getPredicate)
            .reduce(Predicate::and).orElseThrow(() -> new AppException(""));
  }

  static Predicate<CustomerWithMoviesAndSalesStand> filterByMovieReleaseDate(Map.Entry<MovieFilteringCriterion, List<?>> cus) {

    var minLocalDate = LocalDate.parse(String.valueOf(cus.getValue().get(0)));
    var maxLocalDate = LocalDate.parse(String.valueOf(cus.getValue().get(1)));
    return customerWithMoviesAndSalesStand ->
            customerWithMoviesAndSalesStand.getMovieReleaseDate().compareTo(minLocalDate) >= 0 &&
                    customerWithMoviesAndSalesStand.getMovieReleaseDate().compareTo(maxLocalDate) <= 0;
  }

  static Predicate<CustomerWithMoviesAndSalesStand> filterByMovieDurationPredicate(Map.Entry<MovieFilteringCriterion, List<?>> cus) {

    return customerWithMoviesAndSalesStand ->
            customerWithMoviesAndSalesStand.getMovieDuration() >= Integer.parseInt(String.valueOf(cus.getValue().get(0)))
                    && customerWithMoviesAndSalesStand.getMovieDuration() <= Integer.parseInt(String.valueOf(cus.getValue().get(1)));
  }

  static Predicate<CustomerWithMoviesAndSalesStand> filterByMoviesGenre(Map.Entry<MovieFilteringCriterion, List<?>> cus) {
    return customerWithMoviesAndSalesStand -> cus.getValue().stream().map(String::valueOf).
            anyMatch(genre -> customerWithMoviesAndSalesStand.getMovieGenre().equalsIgnoreCase(genre));
  }

  static Predicate<CustomerWithMoviesAndSalesStand> getPredicate(Map.Entry<MovieFilteringCriterion, List<?>> cus) {
    Predicate<CustomerWithMoviesAndSalesStand> predicate;
    switch (cus.getKey()) {
      case DURATION -> predicate = JoinedEntitiesUtils.filterByMovieDurationPredicate(cus);
      case RELEASE_DATE -> predicate = JoinedEntitiesUtils.filterByMovieReleaseDate(cus);
      case GENRE -> predicate = JoinedEntitiesUtils.filterByMoviesGenre(cus);
      default -> throw new AppException("Filtering criterion not recognized");
    }
    return predicate;
  }
}
