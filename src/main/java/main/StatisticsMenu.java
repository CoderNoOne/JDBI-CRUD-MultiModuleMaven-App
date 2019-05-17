package main;

import exceptions.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import utils.MenuOptionsUtils;
import utils.UserDataUtils;

import java.util.Arrays;

public class StatisticsMenu {

  private final CustomerService customerService = new CustomerService(new CustomerRepository());
  private final MovieService movieService = new MovieService(new MovieRepository());
  private final LoyaltyCardService loyaltyCardService = new LoyaltyCardService(new LoyaltyCardRepository());
  private final SalesStandService salesStandService = new SalesStandService(new SalesStandRepository());
  private final JoinedEntitiesService joinedEntitiesService = new JoinedEntitiesService(new JoinedEntitiesRepository());
  private final Logger logger = LoggerFactory.getLogger(StatisticsMenu.class);

  public void menu() {

    MenuOptionsUtils.statisticsOptionsMenu();
    while (true) {
      try {
        int option = UserDataUtils.getInt("INPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> option7_1();
          case 2 -> option7_2();
          case 3 -> option7_3();
          case 4 -> option7_4();
          case 5 -> option7_5();
          case 6 -> option7_6();
          case 7 -> option7_7();
          case 8 -> MenuOptionsUtils.statisticsOptionsMenu();
          case 9 -> new MainMenu().mainMenu();
          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        logger.info(e.getExceptionMessage());
        logger.error(Arrays.toString(e.getStackTrace()));
      }
    }
  }

  private void option7_7() {

  }

  private void option7_6() {

  }

  private void option7_5() {

  }

  private void option7_4() {

  }

  private void option7_3() {

  }

  private void option7_2() {
//    joinedEntitiesService.mostPopularMovieGenreForEachCustomer().forEach((customerId, innerMap) -> {
//      customerService.
//    });
  }

//  "Movies grouped by the most popular ones",
//          "Most popular movie category grouped by each customer ",
//          "The most expensive ticket bought grouped each customer",
//          "The cheapest ticket bought grouped for each customer",
//          "Average ticket price grouped by month",
//          "Total monthly expenses on tickets grouped by month for each customer",
//          "Total amount of tickets bought with discount by movie category and grouped by each customer",
//          "Total amount of tickets bought without discount by movie category and grouped by each customer",
//          "Back to main menu"


  //filmy pogrupowane wg najchętnie kupowanych
  private void option7_1() {
    joinedEntitiesService.movieGroupedByPopularity().forEach((movie, number) -> System.out.println("Movie: " + movie.getTitle()
            + " - > " + number));
  }

}
