package main;

import exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import model.entity.Customer;
import model.entity.Movie;
import model.others.CustomerWithLoyaltyCard;
import repository.entity_repository.impl.CustomerRepository;
import repository.entity_repository.impl.LoyaltyCardRepository;
import repository.entity_repository.impl.MovieRepository;
import repository.entity_repository.impl.SalesStandRepository;
import repository.others.JoinedEntitiesRepository;
import service.entity_service.CustomerService;
import service.entity_service.LoyaltyCardService;
import service.entity_service.MovieService;
import service.entity_service.SalesStandService;
import service.others.DataInitializeService;
import service.others.JoinedEntitiesService;
import utils.others.EmailUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static utils.others.SimulateTimeFlowUtils.*;
import static utils.others.UserDataUtils.*;
import static utils.others.UserDataUtils.printMessage;

@Slf4j
class MainMenu {

  private final CustomerService customerService = new CustomerService(new CustomerRepository());
  private final MovieService movieService = new MovieService(new MovieRepository());
  private final LoyaltyCardService loyaltyCardService = new LoyaltyCardService(new LoyaltyCardRepository());
  private final SalesStandService salesStandService = new SalesStandService(new SalesStandRepository());
  private final JoinedEntitiesService joinedEntitiesService = new JoinedEntitiesService(new JoinedEntitiesRepository());

  void mainMenu() {
    while (true) {
      mainMenuOptions();
      try {
        int option = getInt("INPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> option1();
          case 2 -> option2();
          case 3 -> option3();
          case 4 -> option4TableManagementMenu();
          case 5 -> option5();
          case 6 -> option6();
          case 7 -> option7();
          case 8 -> option8();
          case 9 -> mainMenuOptions();
          case 10 -> {
            close();
            return;
          }

          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        log.error(e.getExceptionMessage());
        log.error(Arrays.toString(e.getStackTrace()));
      }
    }
  }


  private void mainMenuOptions() {

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
                    "Option no. 10 - {9}",


            "Add new Customer",
            "Add new movie from json file",
            "Generate example data for table movies and customers",
            "Movie and customer table management",
            "Buy a ticket",
            "History - summary",
            "Some statistics",
            "Move in time forwardly",
            "Show menu options",
            "Exit the program"

    ));
  }


  private void option1() {

    String name = getString("Input customer name");
    String surname = getString("Input customer surname");
    int age = getInt("Input customer age");
    String email = getString("Input customer email");

    boolean isAdded = customerService.addCustomer(name, surname, age, email);
    if (!isAdded) {
      throw new AppException("Specified customer object couldn't have been added to db");
    }
    printMessage("Customer successfully added to db!");
  }

  private void option2() {

    String jsonFilename = getString("Input json filename");

    if (jsonFilename == null || !jsonFilename.matches(".+\\.json$")) {
      throw new AppException("Wrong json file format");
    }
    boolean isAdded = movieService.addMovie(jsonFilename);

    if (!isAdded) {
      throw new AppException("Movie raw data couldn't be added to db");
    }

  }

  private void option3() {
    DataInitializeService.init();
  }

  private void option4TableManagementMenu() {

    new CustomerAndMovieTableManagementMenu().menu();
  }

  //zakup biletu - nie ma rollbacku?
  private void option5() {

    var customer = customerService.getCustomerFromUserInput();
    var ticketDetails = movieService.chooseMovieStartTime();
    Movie movie = (Movie) ticketDetails.get("movie");
    LocalDateTime movieStartTime = (LocalDateTime) ticketDetails.get("movieStartTime");

    Optional<CustomerWithLoyaltyCard> customerLoyaltyCardId = joinedEntitiesService.getCustomerWithLoyaltyCardByCustomer(customer)/*.get().getLoyaltyCardId()*/;
    salesStandService.addNewSale(movie, customer, movieStartTime);

    if (joinedEntitiesService.doCustomerPosesActiveLoyaltyCard(customer)) {
      loyaltyCardService.decreaseMoviesNumberByLoyaltyCardId(customerLoyaltyCardId.get().getLoyaltyCardId());
      movie.setPrice(movie.getPrice().subtract(loyaltyCardService.findLoyaltyCardById(customerLoyaltyCardId.get().getLoyaltyCardId()).get().getDiscount()));
    } else if (joinedEntitiesService.numberOfMoviesBoughtByCustomer(customer) == loyaltyCardService.getLoyaltyMinMovieCard()) {
      option5Help(customer);
    }

    customerService.update(customer);
    EmailUtils.sendMoviePurchaseConfirmation(customer.getEmail(), "aa", movie, movieStartTime);
  }

  private void option5Help(Customer customer) {
    if (getString("Do you want to add a loyalty card? (y/n)").toUpperCase().equalsIgnoreCase("y")) {
      loyaltyCardService.addLoyaltyCardForCustomer(customer);
      printMessage("Loyalty card successfully added to you account!");
    } else {
      printMessage("Too bad. Maybe next time");
    }

  }

  private void option6() {
    new TransactionHistoryMenu().menu();
  }

  private void option7() {
    new StatisticsMenu().menu();
  }

  private void option8() {
    var noOfDays = getInt("How many days do you want to move in time forwardly?");
    moveDateTimeForwardByDaysNumber(noOfDays);
    printMessage("Present time: " + LocalDateTime.now(getClock()));
  }
}
