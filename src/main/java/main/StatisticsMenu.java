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
import service.others.JoinedEntitiesService;
import utils.others.UserDataUtils;

import java.text.MessageFormat;
import java.util.Arrays;

import static utils.others.UserDataUtils.*;

@Slf4j
class StatisticsMenu {

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
          case 1 -> option7_1();
          case 2 -> option7_2();
          case 3 -> option7_3();
          case 4 -> option7_4();
          case 5 -> option7_5();
          case 6 -> option7_6();
          case 7 -> option7_7();
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
                    "Option no. 9 - {8}",

            "Movies grouped by the most popular ones",
            "The most popular movie genre grouped by each customer ",
            "Map<Category, List<Customer> customer in the age <a,b>",
            "Average movie duration grouped by movie genre",
            "Most expensive movie for each genre",
            "Back to main menu"
    ));
  }


  /*1. Wykonac zestawienie, w ktorym pokazesz kategorie filmu oraz zestawienie klientow, ktorzy zakupili film w tej kategorii i maja ciagle aktywna karte lojanosciowa
2. Wykonac zestawienie w ktorym pokazesz kategorie filmu oraz klientow ktorzy kupili filmy tej kategorii i sa w wieku <,a b>
3. Zestawienie w którym pokazane zostaną kategorie filmy i średnie wartości długości trwania filmu w tej kategorii
4. Zestawienie w którym pokazane zostaną kategori filmu i najdłuższe oraz nakrótsze długości trwania filmu w tej kategorii
5. Kategoria filmu i najwcześniejsza data premiery filmy w tej kategorii
4. Wykonac zestawienie, w ktorym poekazesz kategorie filmu oraz zestawienie tych klientow, ktorych karta traci wartosc przed rozpoczeciem ktoregokolwiek z zakupionych seansow w tej kategorii*/
  private void option7_1() {
    joinedEntitiesService.movieGroupedByPopularity().forEach((movie, number) -> System.out.println("Movie: " + movie.getTitle()
            + " - > " + number));
  }

  private void option7_2() {
    joinedEntitiesService.mostPopularMovieGenreForEachCustomer().forEach((customerId, innerMap) -> {
      printMessage(customerService.findCustomerById(customerId).get().toString() + "\n");
      innerMap.forEach((category, number) -> printMessage("Category: " + category + " -> Bought:" + number + " times."));
      printMessage("\n");
    });
  }

  private void option7_3() {
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

  //Zestawienie w którym pokazane zostaną kategorie filmy i średnie wartości długości trwania filmu w tej kategorii
  private void option7_4() {
    movieService.averageMovieDurationForMovieCategory().forEach((category, averageDuration) -> {
      printMessage("Category: " + category + " -> " + String.format("%.2f", averageDuration));
    });
  }

  //zestawienie w ktorym pokazane w którym pokazane zostaną kategori filmu i najdroższe filmy w tej kategorii
  private void option7_5() {
    movieService.mostExpensiveMoviesForEachGenre().forEach((category, mostExpensiveMovies) -> {
      printMessage("Category: " + category);
      printCollectionWithNumeration(mostExpensiveMovies);
    });
  }

  //zestawienie w ktorym pokazane zostaną kategorie filmu i najtańse filmy w tej kategorii
  private void option7_6() {
    movieService.cheapestMoviesForEachGenre().forEach((category, cheapestMovies) -> {
      printMessage("Category: " + category);
      printCollectionWithNumeration(cheapestMovies);
    });
  }

  //Kategoria filmu i najwcześniejsza data premiery filmy w tej kategorii
  private void option7_7() {
    movieService
  }


  //sredni wiek osob ktore kupiły filmy w danej kategorii
}
