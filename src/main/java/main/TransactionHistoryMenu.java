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
import utils.EmailUtils;
import utils.TicketsFilteringUtils;
import utils.UserDataUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
class TransactionHistoryMenu {

  private final CustomerService customerService = new CustomerService(new CustomerRepository());
  private final MovieService movieService = new MovieService(new MovieRepository());
  private final LoyaltyCardService loyaltyCardService = new LoyaltyCardService(new LoyaltyCardRepository());
  private final SalesStandService salesStandService = new SalesStandService(new SalesStandRepository());
  private final JoinedEntitiesService joinedEntitiesService = new JoinedEntitiesService(new JoinedEntitiesRepository());

  //historia
  void menu() {

    while (true) {
      menuOptions();
      try {
        int option = UserDataUtils.getInt("\nINPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> option1();
          case 2 -> option2();
          case 3 -> option3();

          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        log.error(e.getExceptionMessage());
        log.error(Arrays.toString(e.getStackTrace()));
      }
    }
  }


  private void option1() {
    joinedEntitiesService.allMoviesBoughtSortedAlphabetically().forEach(System.out::println);
  }

  private Map<Integer, Set<Movie>> option2Help() {

    var allCustomers = customerService.showAllCustomers();
    int customerId;

    if (allCustomers.isEmpty()) {
      System.out.println("There are no customers in database yet");
      return Collections.emptyMap();
    }

    do {
      customerId = UserDataUtils.getInt("Choose proper customer id from above list");
    } while (customerService.findCustomerById(customerId).isEmpty());

    var distinctMovies = joinedEntitiesService.allDistinctMoviesBoughtBySpecifiedCustomerSortedAlphabetically(customerId);

    return Collections.singletonMap(customerId, distinctMovies);
  }

  private void option2() {
    var distinctMovies = option2Help().values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    System.out.println("Selected customer bought " + distinctMovies.size() + " tickets for different movies\n");
    optionPrint(distinctMovies);
  }

  private void optionPrint(Set<? extends Object> distinctMovies) {
    AtomicInteger counter = new AtomicInteger(1);
    distinctMovies.forEach((movie) -> System.out.println("No. " + counter.getAndIncrement() + ". " + movie));
  }

  private void option3() {
    var customerId = option2Help().keySet().iterator().next();
    var movieFilters = TicketsFilteringUtils.inputMovieFilters("Specify movie filters").getFilters();
    var filteredCustomerMovies = joinedEntitiesService.getCustomerMoviesByFilters(customerId, movieFilters);
    EmailUtils.sendSummaryTableByFilters(/*"firelight.code@gmail.com"*/customerService.findCustomerById(customerId).get().getEmail(), "From app", new ArrayList<>(filteredCustomerMovies), movieFilters);
    optionPrint(filteredCustomerMovies);
  }

  public void menuOptions() {
    System.out.println(MessageFormat.format(
            "\nOption no. 1 - {0}\n" +
                    "Option no. 2 - {1}\n" +
                    "Option no. 3 - {2}\n" +
                    "Option no. 4 - {3}\n" +
                    "Option no. 5 - {4}",

            "All distinct movies bought by all customers",
            "All distinct movies bought by specified customer",
            "Filter tickets transaction history bought by specified customer",
            "",
            ""
    ));

  }

}
