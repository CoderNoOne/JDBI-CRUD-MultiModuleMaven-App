package main;

import exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import model.entity.Customer;
import model.entity.Movie;
import repository.entity_repository.impl.CustomerRepository;
import repository.entity_repository.impl.LoyaltyCardRepository;
import repository.entity_repository.impl.MovieRepository;
import repository.entity_repository.impl.SalesStandRepository;
import repository.others.JoinedEntitiesRepository;
import service.entity_service.CustomerService;
import service.entity_service.LoyaltyCardService;
import service.entity_service.MovieService;
import service.entity_service.SalesStandService;
import service.others.JoinedEntitiesService;

import utils.UserDataUtils;

import java.text.MessageFormat;
import java.util.*;

import java.util.function.Predicate;
import java.util.stream.Collectors;


import static utils.CustomerSortingUtils.getCustomerSortingAlgorithm;
import static utils.MovieSortingUtils.getMovieSortingAlgorithm;
import static utils.UserDataUtils.*;

@Slf4j
class CustomerAndMovieTableManagementMenu {

  private final CustomerService customerService = new CustomerService(new CustomerRepository());
  private final MovieService movieService = new MovieService(new MovieRepository());
  private final LoyaltyCardService loyaltyCardService = new LoyaltyCardService(new LoyaltyCardRepository());
  private final SalesStandService salesStandService = new SalesStandService(new SalesStandRepository());
  private final JoinedEntitiesService joinedEntitiesService = new JoinedEntitiesService(new JoinedEntitiesRepository());

  void menu() {
    menuOptions();
    while (true) {
      try {
        int option = UserDataUtils.getInt("INPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> option4_1();
          case 2 -> option4_2();
          case 3 -> option4_3();
          case 4 -> option4_4();
          case 5 -> option4_5();
          case 6 -> option4_6();
          case 7 -> option4_7();
          case 8 -> menuOptions();
          case 9 -> new MainMenu().mainMenu();
          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        log.info(e.getExceptionMessage());
        log.error(Arrays.toString(e.getStackTrace()));
      }
    }
  }

  public void menuOptions() {
    System.out.println(MessageFormat.format(
            "\nOption no. 1 - {0}\n" +
                    "Option no. 2 - {1}\n" +
                    "Option no. 3 - {2}\n" +
                    "Option no. 4 - {3}\n" +
                    "Option no. 5 - {4}\n" +
                    "Option no. 6 - {5}\n" +
                    "Option no. 7 - {6}\n" +
                    "Option no. 8 - {7}\n" +
                    "Option no. 9 - {8}",

            "Delete customer",
            "Delete movie",
            "Show all customers", /*czy sortowac*/
            "Shows all movies", /*czy sortowac*/
            "Show movies with duration in the specified time range", /*czy sortowac*/
            "Show customer detail by his email",
            "Show customers with active loyalty card",
            "Show customers with age with specified range", /*czy sortowac*/
            "Update customer", /*wyswietlenie wszystkich customerow + co chcesz updetowac*/
            "Update movie", /*wyswietlenei wszystkich filmow  + co chcesz updatetowac */
            "Back to main menu"
    ));
  }

  private Comparator<Movie> getMovieComparators() {
    return getMovieSortingAlgorithm("Input your movie sorting algorithms").getComparator();
  }

  private Comparator<Customer> getCustomerComparator() {
    return getCustomerSortingAlgorithm("Input your customer sorting algorithms").getComparator();
  }

  //updatetowanie customera
  private void option4_12() {
    printCollectionWithNumeration(customerService.getAllCustomers());

    var customerId = getInt("Choose customer id you want to update");

    Optional<Customer> customerById = customerService.findCustomerById(customerId);
    if(customerById.isEmpty()){
      throw new AppException("There is no customer with such an id in our database!");
    }
    printMessage("Choose customer property you want to change/update");

    customerService.update();

  }

  //updatetowanie movie
  private void option4_11() {
    printMessage("Choose customer property you want to change/update");
  }

  private void option4_10() {

    var minMoviePrice = getBigDecimal("Input minimum movie price");
    var maxMoviePrice = getBigDecimal("Input maximum movie price");

    if (minMoviePrice.compareTo(, maxMoviePrice) > 0) {
      throw new AppException("Minimum movie price cannot be greater than maximum one");
    }

    var moviesWithPriceWithinRange = movieService.getAllMovies().stream().filter(movie -> movie.getPrice().compareTo(minMoviePrice) >= 0 && movie.getPrice().compareTo(maxMoviePrice) <= 0)
            .collect(Collectors.toList());

    var choice = getString("Do you want to sort movies with price between specified time interval (Y/N").equalsIgnoreCase("Y");

    if (choice) {
      moviesWithPriceWithinRange.sort(getMovieComparators());
    }

    printMessage(choice ? "Sorted movies with price between " + minMoviePrice + " and " + maxMoviePrice + "\n" : "Unsorted movies with duration between " + minMoviePrice + " and " + maxMoviePrice + "\n");

    printCollectionWithNumeration(moviesWithPriceWithinRange);
  }

  private void option4_9() {

    var minMovieDuration = getInt("Input minimum movie duration");
    var maxMovieDuration = getInt("Input maximum movie duration");
    if (maxMovieDuration < minMovieDuration) {
      throw new AppException("Minimum movie duration cannot be greater than maximum one");
    }

    var moviesWithDurationWithinRange = movieService.getAllMovies().stream().filter(movie -> movie.getDuration() >= minMovieDuration && movie.getDuration() <= maxMovieDuration)
            .collect(Collectors.toList());

    var choice = getString("Do you want to sort customers with active loyalty card (Y/N").equalsIgnoreCase("Y");

    if (choice) {
      moviesWithDurationWithinRange.sort(getMovieComparators());
    }

    printMessage(choice ? "Sorted movies with duration between " + minMovieDuration + " and " + maxMovieDuration + "\n" : "Unsorted movies with duration between " + minMovieDuration + " and " + maxMovieDuration + "\n");
    printCollectionWithNumeration(moviesWithDurationWithinRange);
  }

  private void option4_8() {

    var choice = getString("Do you want to sort customers with active loyalty card (Y/N").equalsIgnoreCase("Y");

    var customersWithActiveLoyaltyCard = joinedEntitiesService.getCustomersWithLoyaltyCardWithActiveLoyaltyCard().stream().map(obj -> customerService.findCustomerById(obj.getCustomerId()).get())
            .collect(Collectors.toList());
    if (choice) {
      customersWithActiveLoyaltyCard.sort(getCustomerComparator());
    }

    printMessage(choice ? "Sorted customer with active loyalty card \n" : "Unsorted customer with active loyalty card \n");
    printCollectionWithNumeration(customersWithActiveLoyaltyCard);
  }

  private void option4_7() {
    var choice = getString("Do you want to sort all movies bought by all customers (Y/N").equalsIgnoreCase("Y");

    var allMovies = movieService.getAllMovies();
    if (choice) {
      allMovies.sort(getMovieComparators());
    }

    printMessage(choice ? "Sorted movie list \n" : "Unsorted movie list \n");
    printCollectionWithNumeration(allMovies);
  }

  private void option4_6() {
    int movieId = UserDataUtils.getInt("Input movie id");
    movieService.findMovieById(movieId).ifPresent(System.out::println);
  }

  private void option4_5() {
    int customerId = UserDataUtils.getInt("Input customer id");
    movieService.findMovieById(customerId).ifPresent(System.out::println);
  }

  private void option4_4() {
    var minCustomerAge = getInt("Input minimum customer age");
    var maxCustomerAge = getInt("Input maximum customer age");
    if (maxCustomerAge < minCustomerAge) {
      throw new AppException("Minimum age cannot be greater than maximum one");
    }
    printCollectionWithNumeration(customerService.getAllCustomers()
            .stream().filter(customer -> customer.getAge() > minCustomerAge && customer.getAge() < maxCustomerAge)
            .collect(Collectors.toList()));
  }

  private void option4_3() {
    customerService.getAllCustomers();
    var choice = getString("Do you want to sort all customers (Y/N").equalsIgnoreCase("Y");

    var allCustomers = customerService.getAllCustomers();
    if (choice) {
      allCustomers.sort(getCustomerComparator());
    }

    printMessage(choice ? "Sorted customer list \n" : "Unsorted customer list \n");
    printCollectionWithNumeration(allCustomers);
  }

  private void option4_2() {
    Integer integer = UserDataUtils.getInt("Input movie id you want to delete from database");
    movieService.deleteMovie(integer);
  }

  private void option4_1() {
    Integer integer = UserDataUtils.getInt("Input customer id you want to delete from database");
    customerService.deleteCustomer(integer);
  }
}

