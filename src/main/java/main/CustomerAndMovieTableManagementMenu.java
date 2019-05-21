package main;

import exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import model.entity.Customer;
import model.entity.Movie;
import repository.entity_repository.impl.CustomerRepository;
import repository.entity_repository.impl.MovieRepository;
import repository.others.JoinedEntitiesRepository;
import service.entity_service.CustomerService;
import service.entity_service.MovieService;
import service.others.JoinedEntitiesService;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static utils.others.UserDataUtils.*;
import static utils.sorting.CustomerSortingUtils.getCustomerSortingAlgorithm;
import static utils.sorting.MovieSortingUtils.getMovieSortingAlgorithm;
import static utils.update.UpdateCustomerUtils.getUpdatedCustomer;
import static utils.update.UpdateMovieUtils.getUpdatedMovie;

@Slf4j
class CustomerAndMovieTableManagementMenu {

  private final CustomerService customerService = new CustomerService(new CustomerRepository());
  private final MovieService movieService = new MovieService(new MovieRepository());
  private final JoinedEntitiesService joinedEntitiesService = new JoinedEntitiesService(new JoinedEntitiesRepository());

  void showTableManagementMenu() {
    showMenuOptions();
    while (true) {
      try {
        int option = getInt("INPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> deleteCustomerById();
          case 2 -> deleteMovieById();
          case 3 -> showCustomerById();
          case 4 -> showMovieById();
          case 5 -> showAllCustomers();
          case 6 -> showAllMovies();
          case 7 -> showMoviesWithDurationBetween();
          case 8 -> showMoviesWithPriceBetween();
          case 9 -> showCustomersWithActiveLoyaltyCard();
          case 10 -> showCustomersWithAgeBetween();
          case 11 -> updateCustomer();
          case 12 -> updateMovie();
          case 13 -> showMenuOptions();
          case 14 -> new MainMenu().showMainMenu();
          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        log.error(e.getExceptionMessage());
        log.error(Arrays.toString(e.getStackTrace()));
      }
    }
  }

  private void showMenuOptions() {
    System.out.println(MessageFormat.format(
            "\nOption no. 1 - {0}\n" +
                    "Option no. 2 - {1}\n" +
                    "Option no. 3 - {2}\n" +
                    "Option no. 4 - {3}\n" +
                    "Option no. 5 - {4}\n" +
                    "Option no. 6 - {5}\n" +
                    "Option no. 7 - {6}\n" +
                    "Option no. 8 - {7}\n" +
                    "Option no. 9 - {8}\n" +
                    "Option no. 10 - {9}\n" +
                    "Option no. 11 - {10}\n" +
                    "Option no. 12 - {11}\n" +
                    "Option no. 13 - {12}\n" +
                    "Option no. 14 - {13}",

            "Delete customer",
            "Delete movie",
            "Show customer by id",
            "Show movie by id",
            "Show all customers",
            "Shows all movies",
            "Show movies with duration in the specified time range",
            "Show movies with price between",
            "Show customers with active loyalty card",
            "Show customers with age with specified range",
            "Update customer",
            "Update movie",
            "Show menu options",
            "Back to main menu"
    ));
  }

  private Comparator<Movie> getMovieComparators() {
    return getMovieSortingAlgorithm("Input your movie sorting algorithms").getComparator();
  }

  private Comparator<Customer> getCustomerComparator() {
    return getCustomerSortingAlgorithm("Input your customer sorting algorithms").getComparator();
  }


  private void updateCustomer() {
    printCollectionWithNumeration(customerService.getAllCustomers());

    var customerId = getInt("Choose customer id you want to update");

    Optional<Customer> customerById = customerService.findCustomerById(customerId);
    if (customerById.isEmpty()) {
      throw new AppException("There is no customer with such an id in our database!");
    }
    printMessage(customerService.updateCustomer(getUpdatedCustomer(customerById.get())) ?
            "Customer has been updated successfully" : "Some of new customer's field failed to pass the validation. Check the output");
  }


  private void updateMovie() {

    printCollectionWithNumeration(movieService.getAllMovies());

    var movieId = getInt("Choose movie id you want to update");

    Optional<Movie> movieById = movieService.findMovieById(movieId);
    if (movieById.isEmpty()) {
      throw new AppException("There is no movie with such an id in our database!");
    }
    printMessage(movieService.updateMovie(getUpdatedMovie(movieById.get())) ?
            "Movie has been updated successfully" : "Some of new movie's field failed to pass the validation. Check the output");

  }

  private void showMoviesWithPriceBetween() {

    var minMoviePrice = getBigDecimal("Input minimum movie price");
    var maxMoviePrice = getBigDecimal("Input maximum movie price");

    if (minMoviePrice.compareTo(maxMoviePrice) > 0) {
      throw new AppException("Minimum movie price cannot be greater than maximum one");
    }

    var moviesWithPriceWithinRange = movieService.getAllMovies().stream()
            .filter(movie -> movie.getPrice().compareTo(minMoviePrice) >= 0 && movie.getPrice().compareTo(maxMoviePrice) <= 0)
            .collect(Collectors.toList());

    var choice = getString("Do you want to sort movies with price between specified time interval (Y/N").equalsIgnoreCase("Y");

    if (choice) {
      moviesWithPriceWithinRange.sort(getMovieComparators());
    }

    printMessage(choice ? "Sorted movies with price between " + minMoviePrice + " and " + maxMoviePrice
            + "\n" : "Unsorted movies with duration between " + minMoviePrice + " and " + maxMoviePrice + "\n");

    printCollectionWithNumeration(moviesWithPriceWithinRange);
  }

  private void showMoviesWithDurationBetween() {

    var minMovieDuration = getInt("Input minimum movie duration");
    var maxMovieDuration = getInt("Input maximum movie duration");
    if (maxMovieDuration < minMovieDuration) {
      throw new AppException("Minimum movie duration cannot be greater than maximum one");
    }

    var moviesWithDurationWithinRange = movieService.getAllMovies().stream()
            .filter(movie -> movie.getDuration() >= minMovieDuration && movie.getDuration() <= maxMovieDuration)
            .collect(Collectors.toList());

    var choice = getString("Do you want to sort movies (Y/N").equalsIgnoreCase("Y");

    if (choice) {
      moviesWithDurationWithinRange.sort(getMovieComparators());
    }

    printMessage(choice ? "Sorted movies with duration between " + minMovieDuration + " and " + maxMovieDuration +
            "\n" : "Unsorted movies with duration between " + minMovieDuration + " and " + maxMovieDuration + "\n");
    printCollectionWithNumeration(moviesWithDurationWithinRange);
  }

  private void showCustomersWithActiveLoyaltyCard() {

    var choice = getString("Do you want to sort customers with active loyalty card (Y/N").equalsIgnoreCase("Y");

    var customersWithActiveLoyaltyCard = joinedEntitiesService.getCustomersWithActiveLoyaltyCard()
            .stream().map(obj -> customerService.findCustomerById(obj.getCustomerId()).get())
            .collect(Collectors.toList());
    if (choice) {
      customersWithActiveLoyaltyCard.sort(getCustomerComparator());
    }

    printMessage(choice ? "Sorted customer with active loyalty card \n" : "Unsorted customer with active loyalty card \n");
    printCollectionWithNumeration(customersWithActiveLoyaltyCard);
  }

  private void showAllMovies() {
    var choice = getString("Do you want to sort all movies bought by all customers (Y/N").equalsIgnoreCase("Y");

    var allMovies = movieService.getAllMovies();
    if (choice) {
      allMovies.sort(getMovieComparators());
    }

    printMessage(choice ? "Sorted movie list \n" : "Unsorted movie list \n");
    printCollectionWithNumeration(allMovies);
  }

  private void showMovieById() {
    int movieId = getInt("Input movie id");
    movieService.findMovieById(movieId).ifPresent(System.out::println);
  }

  private void showCustomerById() {
    int customerId = getInt("Input customer id");
    customerService.findCustomerById(customerId).ifPresent(System.out::println);
  }

  private void showCustomersWithAgeBetween() {
    var minCustomerAge = getInt("Input minimum customer age");
    var maxCustomerAge = getInt("Input maximum customer age");
    if (maxCustomerAge < minCustomerAge) {
      throw new AppException("Minimum age cannot be greater than maximum one");
    }
    printCollectionWithNumeration(customerService.getAllCustomers()
            .stream().filter(customer -> customer.getAge() > minCustomerAge && customer.getAge() < maxCustomerAge)
            .collect(Collectors.toList()));
  }

  private void showAllCustomers() {
    customerService.getAllCustomers();
    var choice = getString("Do you want to sort all customers (Y/N").equalsIgnoreCase("Y");

    var allCustomers = customerService.getAllCustomers();
    if (choice) {
      allCustomers.sort(getCustomerComparator());
    }

    printMessage(choice ? "Sorted customer list \n" : "Unsorted customer list \n");
    printCollectionWithNumeration(allCustomers);
  }

  private void deleteMovieById() {
    Integer integer = getInt("Input movie id you want to delete from database");
    movieService.deleteMovie(integer);
  }

  private void deleteCustomerById() {
    Integer integer = getInt("Input customer id you want to delete from database");
    customerService.deleteCustomer(integer);
  }
}

