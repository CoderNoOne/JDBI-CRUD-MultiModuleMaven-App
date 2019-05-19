//package main;
//
//import exceptions.AppException;
//import lombok.extern.slf4j.Slf4j;
//import repository.entity_repository.impl.CustomerRepository;
//import repository.entity_repository.impl.LoyaltyCardRepository;
//import repository.entity_repository.impl.MovieRepository;
//import repository.entity_repository.impl.SalesStandRepository;
//import repository.others.JoinedEntitiesRepository;
//import service.entity_service.CustomerService;
//import service.entity_service.LoyaltyCardService;
//import service.entity_service.MovieService;
//import service.entity_service.SalesStandService;
//import service.others.DataInitializeService;
//
//import service.others.JoinedEntitiesService;
//import utils.MenuOptionsUtils;
//import utils.others.SimulateTimeFlowUtils;
//import utils.others.UserDataUtils;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//
//class Menu {
//
//  private final CustomerService customerService = new CustomerService(new CustomerRepository());
//  private final MovieService movieService = new MovieService(new MovieRepository());
//  private final LoyaltyCardService loyaltyCardService = new LoyaltyCardService(new LoyaltyCardRepository());
//  private final SalesStandService salesStandService = new SalesStandService(new SalesStandRepository());
//  private final JoinedEntitiesService joinedEntitiesService = new JoinedEntitiesService(new JoinedEntitiesRepository());
//
//   void mainMenu() {
//    MenuOptionsUtils.mainMenuOptions();
//    while (true) {
//      try {
//        int option = UserDataUtils.getInt("INPUT YOUR OPTION: ");
//        switch (option) {
//          case 1 -> option1();
//          case 2 -> option2();
//          case 3 -> option3();
//          case 4 -> option4TableManagementMenu();
//          case 5 -> option5();
//          case 6 -> option6();
//          case 7 -> option7();
//          case 8 -> MenuOptionsUtils.mainMenuOptions();
//          case 9 -> {
//            UserDataUtils.close();
//            return;
//          }
//          case 10 -> option10();
//          case 11 -> option11();
//          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
//        }
//      } catch (AppException e) {
//        System.out.println(e.getExceptionMessage());
//        System.err.println(Arrays.toString(e.getStackTrace()));
//      }
//    }
//  }
//
//  private void option11() {
//    System.out.println(LocalDateTime.now(SimulateTimeFlowUtils.getClock()));
//  }
//
//  private void option10() {
//    var noOfDays = UserDataUtils.getInt("How many days do you want to move in time forwardly?");
//
//    SimulateTimeFlowUtils.moveDateTimeForwardByDaysNumber(noOfDays);
//  }
//
//  private void option1() {
//
//    String name = UserDataUtils.getString("Input customer name");
//    String surname = UserDataUtils.getString("Input customer surname");
//    int age = UserDataUtils.getInt("Input customer age");
//    String email = UserDataUtils.getString("Input customer email");
//
//    boolean isAdded = customerService.addCustomer(name, surname, age, email);
//    if (!isAdded) {
//      throw new AppException("Specified customer object couldn't have been added to db");
//    }
//  }
//
//  private void option2() {
//
//    String jsonFilename = UserDataUtils.getString("Input json filename");
//
//    if (jsonFilename == null || !jsonFilename.matches(".+\\.json$")) {
//      throw new AppException("Wrong json file format");
//    }
//    boolean isAdded = movieService.addMovie(jsonFilename);
//
//    if (!isAdded) {
//      throw new AppException("Movie raw data couldn't be added to db");
//    }
//
//  }
//
//  private void option3() {
//    DataInitializeService.init();
//  }
//
//  private void option4TableManagementMenu() {
//    MenuOptionsUtils.customerAndMovieTableManagmentMenu();
//    while (true) {
//      try {
//        int option = UserDataUtils.getInt("INPUT YOUR OPTION: ");
//        switch (option) {
//          case 1 -> option4_1();
//          case 2 -> option4_2();
//          case 3 -> option4_3();
//          case 4 -> option4_4();
//          case 5 -> option4_5();
//          case 6 -> option4_6();
//          case 7 -> option4_7();
//          case 8 -> MenuOptionsUtils.customerAndMovieTableManagmentMenu();
//          case 9 -> mainMenu();
//          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
//        }
//      } catch (AppException e) {
//        System.out.println(e.getExceptionMessage());
//        System.err.println(Arrays.toString(e.getStackTrace()));
//      }
//    }
//  }
//
//  private void option4_7() {
//
//  }
//
//  private void option4_6() {
//    int movieId = UserDataUtils.getInt("Input movie id");
//    movieService.findMovieById(movieId).ifPresent(System.out::println);
//  }
//
//  private void option4_5() {
//    int customerId = UserDataUtils.getInt("Input customer id");
//    movieService.findMovieById(customerId).ifPresent(System.out::println);
//  }
//
//  private void option4_4() {
//    movieService.getAllMovies();
//  }
//
//  private void option4_3() {
//    customerService.getAllCustomers();
//  }
//
//  private void option4_2() {
//    Integer integer = UserDataUtils.getInt("Input movie id you want to delete from database");
//    movieService.deleteMovie(integer);
//  }
//
//  private void option4_1() {
//    Integer integer = UserDataUtils.getInt("Input customer id you want to delete from database");
//    customerService.deleteCustomer(integer);
//  }
//
//  //zakup biletu - dodac sprawdzenie czy jest znizka i aktualziwowac loyaltyCard movie numbers dla klienta
//  private void option5() {
//    var customer = customerService.getCustomerFromUserInput();
//    movieService.chooseMovieToWatch();
//
//    var movie = movieService.chooseMovieById();
//    var movieStartTime = movieService.chooseMovieStartTime(movie);
//
//    salesStandService.addSalesStand(movie.getId(), customer.getId(), movieStartTime);
//    var ticketsNumber = salesStandService.ticketsNumberBoughtByCustomerId(customer.getId());
//
//    //jeszcze dorobic wysyłanie maila wiadomość email,
//    // w której podajemy dane filmu, na który zakupiono bilet, godzinę rozpoczęcia oraz cenę biletu po uwzględnieniu zniżek.
//
//    loyaltyCardService.isTransactionDone(customer, ticketsNumber, movie, movieStartTime);
////    if (!loyaltyCardService.doCustomerPosesActiveLoyaltyCardByCustomerId(customer.getId())) {
////      loyaltyCardService.verifyIfCustomerCanGetLoyaltyCard(ticketsNumber, customer.getId());
////      customer.setLoyaltyCardId(loyaltyCardService.getNewlyCreatedLoyaltyCardId());
//////      customerService.update(customer);
////    } else {
////      //zmeniejsze liczbę moviesNumber w loyaltyCard dla danego klienta o 1
////
////      loyaltyCardService.decreaseMoviesNumberByLoyaltyCardId(loyaltyCardId);
////    }
//    customerService.update(customer);
//  }
//
//  //historia
//  private void option6() {
//
//  }
//
//  //statystyki
//  private void option7() {
//
//    MenuOptionsUtils.statisticsOptionsMenu();
//    while (true) {
//      try {
//        int option = UserDataUtils.getInt("INPUT YOUR OPTION: ");
//        switch (option) {
//          case 1 -> option7_1();
//          case 2 -> option7_2();
//          case 3 -> option7_3();
//          case 4 -> option7_4();
//          case 5 -> option7_5();
//          case 6 -> option7_6();
//          case 7 -> option7_7();
//          case 8 -> MenuOptionsUtils.statisticsOptionsMenu();
//          case 9 -> mainMenu();
//          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
//        }
//      } catch (AppException e) {
//        System.out.println(e.getExceptionMessage());
//        System.err.println(Arrays.toString(e.getStackTrace()));
//      }
//    }
//  }
//
//  private void option7_7() {
//
//  }
//
//  private void option7_6() {
//
//  }
//
//  private void option7_5() {
//
//  }
//
//  private void option7_4() {
//
//  }
//
//  private void option7_3() {
//
//  }
//
//  private void option7_2() {
//    joinedEntitiesService.mostPopularMovieGenreForEachCustomer().forEach((customerId, innerMap) -> {
//      customerService.
//    });
//  }
//
////  "Movies grouped by the most popular ones",
////          "Most popular movie category grouped by each customer ",
////          "The most expensive ticket bought grouped each customer",
////          "The cheapest ticket bought grouped for each customer",
////          "Average ticket price grouped by month",
////          "Total monthly expenses on tickets grouped by month for each customer",
////          "Total amount of tickets bought with discount by movie category and grouped by each customer",
////          "Total amount of tickets bought without discount by movie category and grouped by each customer",
////          "Back to main menu"
//
//
//  //filmy pogrupowane wg najchętnie kupowanych
//  private void option7_1() {
//    joinedEntitiesService.movieGroupedByPopularity().forEach((movie, number) -> System.out.println("Movie: " + movie.getTitle()
//            + " - > " + number));
//  }
//
//}
//
//
