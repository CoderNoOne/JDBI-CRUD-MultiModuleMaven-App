package main;

import exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
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
import utils.SimulateTimeFlowUtils;
import utils.UserDataUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;

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
        int option = UserDataUtils.getInt("INPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> option1();
          case 2 -> option2();
          case 3 -> option3();
          case 4 -> option4TableManagementMenu();
          case 5 -> option5();
          case 6 -> option6();
          case 7 -> option7();
          case 8 -> mainMenuOptions();
          case 9 -> {
            UserDataUtils.close();
            return;
          }
          case 10 -> option10();
          case 11 -> option11();
          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        log.error(e.getExceptionMessage());
        log.error(Arrays.toString(e.getStackTrace()));
      }
    }
  }

  private void option5() {

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
                    "Option no. 10 - {9}\n" +
                    "Option no. 11 - {10}",

            "Add new Customer",
            "Add new movie from json file",
            "Generate example data for table movies and customers",
            "Movie and customer table management",
            "Buy a ticket",
            "History - summary",
            "Some statistics",
            "Show menu options",
            "Exit the program"

    ));
  }

  private void option7() {
    new StatisticsMenu().menu();
  }

  private void option6() {
    new TransactionHistoryMenu().menu();
  }


  private void option1() {

    String name = UserDataUtils.getString("Input customer name");
    String surname = UserDataUtils.getString("Input customer surname");
    int age = UserDataUtils.getInt("Input customer age");
    String email = UserDataUtils.getString("Input customer email");

    boolean isAdded = customerService.addCustomer(name, surname, age, email);
    if (!isAdded) {
      throw new AppException("Specified customer object couldn't have been added to db");
    }
  }

  private void option2() {

    String jsonFilename = UserDataUtils.getString("Input json filename");

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

  //zakup biletu - dodac sprawdzenie czy jest znizka i aktualziwowac loyaltyCard movie numbers dla klienta ale nie moze zmeniejsza poniezej 0
//  private void option5() {
//
//    var customer = customerService.getCustomerFromUserInput();
//    var ticketDetails = movieService.chooseMovieStartTime();
//
//    if (salesStandService.isTransactionDone((Movie) ticketDetails.get("movie"), customer, (LocalDateTime) ticketDetails.get("movieStartTime"))) {
//      Integer ticketsNumber = joinedEntitiesService.ticketsNumberBoughtByCustomerId(customer.getId());
//      loyaltyCardService.manageLoyaltyCard(customer, ticketsNumber, (Movie) ticketDetails.get("movie"), (LocalDateTime) ticketDetails.get("movieStartTime"));
//      customerService.update(customer);
//    } else {
//      throw new AppException("Movie start date time is not valid");
//    }
//
//  }

  private void option11() {
    System.out.println(LocalDateTime.now(SimulateTimeFlowUtils.getClock()));
  }

  private void option10() {
    var noOfDays = UserDataUtils.getInt("How many days do you want to move in time forwardly?");

    SimulateTimeFlowUtils.moveDateTimeForwardByDaysNumber(noOfDays);
  }
}
