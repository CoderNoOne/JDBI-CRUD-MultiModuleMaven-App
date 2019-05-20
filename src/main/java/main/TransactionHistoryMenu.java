package main;

import exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import model.entity.Customer;
import model.entity.Movie;
import model.others.CustomerWithMoviesAndSalesStand;
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
import utils.others.EmailUtils;
import utils.others.TicketsFilteringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static utils.others.UserDataUtils.*;
import static utils.others.UserDataUtils.printCollectionWithNumeration;
import static utils.others.UserDataUtils.printMessage;

@Slf4j
class TransactionHistoryMenu {

  private final CustomerService customerService = new CustomerService(new CustomerRepository());
  private final MovieService movieService = new MovieService(new MovieRepository());
  private final LoyaltyCardService loyaltyCardService = new LoyaltyCardService(new LoyaltyCardRepository());
  private final SalesStandService salesStandService = new SalesStandService(new SalesStandRepository());
  private final JoinedEntitiesService joinedEntitiesService = new JoinedEntitiesService(new JoinedEntitiesRepository());

  void menu() {

    while (true) {
      menuOptions();
      try {
        int option = getInt("\nINPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> option1();
          case 2 -> option2();
          case 3 -> option3();
          case 4 -> option4();
          case 5 -> new MainMenu().mainMenu();

          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        log.error(e.getExceptionMessage());
        log.error(Arrays.toString(e.getStackTrace()));
      }
    }
  }

  private void option1() {
    joinedEntitiesService.allDistinctMoviesBoughtSortedAlphabetically().forEach(System.out::println);
  }

  private Map<Integer, Set<Movie>> option2Help() {

    var allCustomers = customerService.getAllCustomers();
    printCollectionWithNumeration(allCustomers);
    int customerId;

    if (allCustomers.isEmpty()) {
      printMessage("There are no customers in database yet");
      return Collections.emptyMap();
    }

    do {
      customerId = getInt("Choose proper customer id from above list");
    } while (customerService.findCustomerById(customerId).isEmpty());

    var distinctMovies = joinedEntitiesService.allDistinctMoviesBoughtBySpecifiedCustomerSortedAlphabetically(customerId);

    return Collections.singletonMap(customerId, distinctMovies);
  }

  private void option2() {
    var distinctMovies = option2Help().values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    System.out.println("Selected customer bought " + distinctMovies.size() + " tickets for different movies\n");
    printCollectionWithNumeration(distinctMovies);
  }

  private void option3() {
    var customerId = option2Help().keySet().iterator().next();
    var movieFilters = TicketsFilteringUtils.inputMovieFilters("Specify movie filters").getFilters();
    var filteredCustomerMovies = joinedEntitiesService.getCustomerMoviesByFilters(customerId, movieFilters);
    printCollectionWithNumeration(filteredCustomerMovies);
    EmailUtils.sendSummaryTableByFilters(/*"firelight.code@gmail.com"*/customerService.findCustomerById(customerId).get().getEmail(), "From app", new ArrayList<>(filteredCustomerMovies), movieFilters);
  }

  private void option4() {
    var customerId = option2Help().keySet().iterator().next();
    List<CustomerWithMoviesAndSalesStand> movies = joinedEntitiesService.allMoviesBoughtByCustomer(customerId);
    Customer customer = customerService.findCustomerById(customerId).get();
    printMessage("All movies bought by customer: " + customer);
    printCollectionWithNumeration(movies);
    EmailUtils.sendAllSummaryTable(customer.getEmail(),"All bought movies", movies);
  }

  private void menuOptions() {
    printMessage(MessageFormat.format(
            "\nOption no. 1 - {0}\n" +
                    "Option no. 2 - {1}\n" +
                    "Option no. 3 - {2}\n" +
                    "Option no. 4 - {3}",

            "All distinct movies bought by all customers",
            "All distinct movies bought by specified customer",
            "Filter tickets transaction history bought by specified customer",
            "All movies bought by specified customer",
            "Back to main menu"
    ));
  }
}
