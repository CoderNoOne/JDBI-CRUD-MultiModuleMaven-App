package main;

import converters.impl.MovieJsonConverter;
import entity_repository.impl.CustomerRepository;
import entity_repository.impl.LoyaltyCardRepository;
import entity_repository.impl.MovieRepository;
import entity_repository.impl.SalesStandRepository;
import entity_service.CustomerService;
import entity_service.LoyaltyCardService;
import entity_service.MovieService;
import entity_service.SalesStandService;
import exceptions.AppException;
import lombok.extern.log4j.Log4j;
import entity.Movie;
import others.DataInitializeService;
import others.JoinedEntitiesRepository;
import others.JoinedEntitiesService;

import others.EmailUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static others.SimulateTimeFlowUtils.*;
import static others.UserDataUtils.*;


@Log4j
class MainMenu {

  private final CustomerService customerService = new CustomerService(new CustomerRepository());
  private final MovieService movieService = new MovieService(new MovieRepository());
  private final LoyaltyCardService loyaltyCardService = new LoyaltyCardService(new LoyaltyCardRepository());
  private final SalesStandService salesStandService = new SalesStandService(new SalesStandRepository());
  private final JoinedEntitiesService joinedEntitiesService = new JoinedEntitiesService(new JoinedEntitiesRepository());

  void showMainMenu() {
    showMainMenuOptions();
    while (true) {
      try {
        int option = getInt("INPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> addCustomer();
          case 2 -> addMovieFromJsonFile();
          case 3 -> generateExampleData();
          case 4 -> showCustomerAndMovieTableManagementMenu();
          case 5 -> buyTicket();
          case 6 -> showTransactionHistoryMenu();
          case 7 -> showStatisticsMenu();
          case 8 -> moveForwardInTime();
          case 9 -> moveBackwardInTime();
          case 10 -> showMainMenuOptions();
          case 11 -> {
            close();
            return;
          }
          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        log.info(e.getExceptionMessage());
        log.error(Arrays.toString(e.getStackTrace()));
      }
    }
  }

  private void showMainMenuOptions() {

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
                    "Option no. 10 - {9}\n" +
                    "Option no. 11 - {10}",

            "Add new Customer",
            "Add new movie from json file",
            "Generate example data for table movies and customers",
            "Movie and customer table management",
            "Buy a ticket",
            "History - summary",
            "Some statistics",
            "Move in time forwardly",
            "Move in time backwardly",
            "Show main menu options",
            "Exit the program"
    ));
  }

  private void addCustomer() {

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

  private void addMovieFromJsonFile() {

    String jsonFilename = getString("Input json filename");

    if (!jsonFilename.matches(".+\\.json$")) {
      throw new AppException("Wrong json file format");
    }

    boolean isAdded = movieService
            .addMovie(new MovieJsonConverter(String.format("./service/example_data/%s", jsonFilename))
                    .fromJson()
                    .orElseThrow(() -> new AppException(String.format("File %s is empty", jsonFilename))));

    if (!isAdded) {
      throw new AppException("Movie couldn't be added to db");
    }
    printMessage("Movie successfully added to db!");
  }

  private void generateExampleData() {
    new DataInitializeService().init();
  }

  private void showCustomerAndMovieTableManagementMenu() {

    new CustomerAndMovieTableManagementMenu().showTableManagementMenu();
  }

  private Movie chooseMovieById() {

    Integer movieIndex;

    printMessage("AVAILABLE MOVIES");
    List<Movie> allMovies = movieService.getAllMovies();
    printCollectionWithNumeration(allMovies);

    do {
      movieIndex = getInt("Input proper movie number");
    } while (movieIndex < 0 || movieIndex > allMovies.size() - 1);

    return allMovies.get(movieIndex);
  }

  private void buyTicket() {

    customerService.getAllCustomers();

    var name = getString("Input your name");
    var surname = getString("Input your surname");
    var email = getString("Input your email");

    var customer = customerService.getCustomerFromUserInput(name, surname, email);
    Movie movie = chooseMovieById();
    LocalDateTime movieStartTime = chooseMovieStartTime(movie);

    if (joinedEntitiesService.doCustomerPosesActiveLoyaltyCard(customer)) {

      joinedEntitiesService
              .getCustomerWithLoyaltyCardByCustomer(customer)
              .ifPresent(customerWithLoyaltyCard -> {
                loyaltyCardService.decreaseMoviesNumberByLoyaltyCardId(customerWithLoyaltyCard.getLoyaltyCardId());
                movie.setPrice(movie
                        .getPrice()
                        .subtract(loyaltyCardService.findLoyaltyCardById(customerWithLoyaltyCard.getLoyaltyCardId()).get().getDiscount()));
              });

    } else if (joinedEntitiesService.numberOfMoviesBoughtByCustomer(customer) >= loyaltyCardService.getLoyaltyMinMovieCard() &&
            getString("Do you want to add a loyalty card? (y/n)").toUpperCase().equalsIgnoreCase("y")) {
      loyaltyCardService.addLoyaltyCardForCustomer(customer);
    }

    salesStandService.addNewSale(movie.getId(), customer.getId(), movieStartTime);
    customerService.updateCustomer(customer);
    EmailUtils.sendMoviePurchaseConfirmation(customer.getEmail(), "MOVIE PURCHASE DETAILS FROM APP", movie, movieStartTime);
  }

  private LocalDateTime chooseMovieStartTime(Movie movie) {

    printMessage("Possible movie show times in the next 24 hours");
    List<LocalDateTime> showTimes = getPossibleShowTimes(movie);
    printCollectionWithNumeration(showTimes);

    int timeNumber;

    do {
      timeNumber = getInt("Input proper movie start time: choose number associated to that time");
    } while (timeNumber < 0 || timeNumber > showTimes.size() - 1);

    return showTimes.get(timeNumber - 1);

  }

  private List<LocalDateTime> getPossibleShowTimes(Movie movie) {

    var presentDateTime = LocalDateTime.now(getClock());

    var localDateTimeToClosesHourOrHalfAnHour = presentDateTime.getMinute() < 30 ? presentDateTime.truncatedTo(ChronoUnit.HOURS).plusMinutes(30) : presentDateTime.plusHours(1).truncatedTo(ChronoUnit.HOURS);
    var seedDateTime = movie.getReleaseDate().compareTo(presentDateTime.toLocalDate()) < 0 ? localDateTimeToClosesHourOrHalfAnHour : movie.getReleaseDate().atTime(8, 0);

    return Stream.iterate(seedDateTime, date -> date.plusMinutes(30))
            .limit(ChronoUnit.HOURS.between(seedDateTime, seedDateTime.plusHours(48)))
            .filter(date -> date.toLocalTime().compareTo(LocalTime.of(8, 0)) >= 0 && date.toLocalTime().compareTo(LocalTime.of(22, 30)) <= 0)
            .collect(Collectors.toList());
  }

  private void showTransactionHistoryMenu() {
    new TransactionHistoryMenu().menu();
  }

  private void showStatisticsMenu() {
    new StatisticsMenu().menu();
  }

  private void moveForwardInTime() {
    var noOfDays = getInt("How many days do you want to move in time forwardly?");
    moveDateTimeForwardByDaysNumber(noOfDays);
    printMessage("Present time: " + LocalDateTime.now(getClock()));
  }

  private void moveBackwardInTime() {
    var noOfDays = getInt("How many days do you want to move backward in time?");
    moveDateTimeBackwardByDaysNumber(noOfDays);
    printMessage("The present time is: " + LocalDateTime.now(getClock()));
  }
}
