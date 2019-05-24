package main;

import exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import repository.entity_repository.impl.CustomerRepository;
import repository.entity_repository.impl.MovieRepository;
import repository.others.JoinedEntitiesRepository;
import service.entity_service.CustomerService;
import service.entity_service.MovieService;
import service.others.JoinedEntitiesService;

import java.text.MessageFormat;
import java.util.Arrays;

import static utils.others.UserDataUtils.*;

@Slf4j
class StatisticsMenu {

  private final CustomerService customerService = new CustomerService(new CustomerRepository());
  private final MovieService movieService = new MovieService(new MovieRepository());
  private final JoinedEntitiesService joinedEntitiesService = new JoinedEntitiesService(new JoinedEntitiesRepository());

  void menu() {
    menuOptions();
    while (true) {
      try {
        int option = getInt("INPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> showMoviesPopularity();
          case 2 -> showMostPopularMoviesForEachCustomer();
          case 3 -> showMoviesBoughtByCustomerWithAgeBetween();
          case 4 -> showAverageMovieDurationForEachGenre();
          case 5 -> showMostExpensiveMovieInEachGenre();
          case 6 -> showTheCheapestMovieInEachGenre();
          case 7 -> showTheEarliestReleaseDateInEachMovieGenre();
          case 8 -> showTheAverageCustomerAgeWhichBoughtAtLeastNumberOfTicketsGroupedByMovieGenre();
          case 9 -> menuOptions();
          case 10 -> new MainMenu().showMainMenu();
          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        log.info(e.getExceptionMessage());
        log.error(Arrays.toString(e.getStackTrace()));
      }
    }
  }

  private void menuOptions() {

    printMessage(MessageFormat.format(
            "\nOption no. 1 - {0}\n" +
                    "Option no. 2 - {1}\n" +
                    "Option no. 3 - {2}\n" +
                    "Option no. 4 - {3}\n" +
                    "Option no. 5 - {4}\n" +
                    "Option no. 6 - {5}\n" +
                    "Option no. 7 - {6}\n" +
                    "Option no. 8 - {7}\n" +
                    "Option no. 9 - {8}\n" +
                    "Option no. 10 - {9}",

            "Movies grouped by popularity",
            "The most popular movie genre grouped by each customer ",
            "Show customers with age between <a,b> and group them by movie genre",
            "Average movie duration grouped by movie genre",
            "The most expensive ticket for each movie genre",
            "The cheapest ticket for each movie genre",
            "Show the earliest release date for each movie genre and show corresponding movies",
            "Show the average age of customers which bought at least specified number of tickets for movies grouped by movie genre",
            "Show the menu options",
            "Back to main menu"
    ));
  }

  private void showMoviesPopularity() {
    joinedEntitiesService.movieGroupedByPopularity().forEach((movie, number) ->
            printMessage(("Movie: " + movie.getTitle() + " - > " + number)));
  }

  private void showMostPopularMoviesForEachCustomer() {
    joinedEntitiesService.mostPopularMovieGenreForEachCustomer().forEach((customerId, innerMap) -> {
      printMessage(customerService.findCustomerById(customerId).get().toString() + "\n");
      innerMap.forEach((category, number) -> printMessage("Category: " + category + " -> Bought:" + number + " times."));
      printMessage("\n");
    });
  }

  private void showMoviesBoughtByCustomerWithAgeBetween() {
    int customerMinAge = getInt("Type customer minimum age");
    int customerMaxAge = getInt("Type customer maximum age");

    if (customerMaxAge < customerMinAge) {
      throw new AppException("Min age cannot be greater than max age!");
    }
    joinedEntitiesService.customersWhoBoughtMoviesWithCategoryAndWithAgeWithinRange(customerMinAge, customerMaxAge)
            .forEach((category, customerList) -> {
              printMessage("Category: " + category + "\n");
              if (customerList.isEmpty()) {
                printMessage("No movies with that category was bought by customers within age " + customerMinAge + " and " + customerMaxAge + "\n");
              } else {
                printCollectionWithNumeration(customerList);
                printMessage("\n");
              }
            });
  }

  private void showAverageMovieDurationForEachGenre() {
    movieService.getAverageMovieDurationForMovieCategory().forEach((category, averageDuration) -> {
      printMessage("Category: " + category + " -> " + String.format("%.2f", averageDuration));
    });
  }

  private void showMostExpensiveMovieInEachGenre() {
    movieService.mostExpensiveMoviesForEachGenre().forEach((category, mostExpensiveMovies) -> {
      printMessage("Category: " + category);
      printCollectionWithNumeration(mostExpensiveMovies);
    });
  }

  private void showTheCheapestMovieInEachGenre() {
    movieService.cheapestMoviesForEachGenre().forEach((category, cheapestMovies) -> {
      printMessage("Category: " + category);
      printCollectionWithNumeration(cheapestMovies);
    });
  }


  private void showTheEarliestReleaseDateInEachMovieGenre() {
    movieService.theEarliestPremierInMovieGenre().forEach((genre, innerMap) -> {
      printMessage("\nIn movie genre: " + genre + " the earliest premiere (release date) falls on " + innerMap.keySet().iterator().next() +
              "\nThe movies are: ");
      printCollectionWithNumeration(innerMap.values());
    });
  }


  private void showTheAverageCustomerAgeWhichBoughtAtLeastNumberOfTicketsGroupedByMovieGenre() {

    int minNoOfMovies = getInt("Type minimum number of movies every customer should have bought to be considered into statistics calc");
    joinedEntitiesService.getAverageCustomerAgeWhichBoughtAtLeastSpecifiedNumberOfTicketGroupedForEachMovieGenre(minNoOfMovies)
            .forEach((genre, averageAge) ->
                    printMessage("Movie genre: " + genre + " -> " + (averageAge != 0 ? String.format("%.2f", averageAge) : "No customer bought at least " + minNoOfMovies + " movies in that category")));
  }

}
