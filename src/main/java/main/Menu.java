package main;

import exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import service.EntityService;
import service.UserDataUtils;

import java.text.MessageFormat;
import java.util.Arrays;

@Slf4j
public class Menu {

  private final UserDataUtils userDataUtils = new UserDataUtils();
  private final EntityService entityService = new EntityService();

  public void mainMenu() {
    menuOptions();
    while (true) {
      try {
        int option = userDataUtils.getInt("INPUT YOUR OPTION: ");
        switch (option) {
          case 1 -> option1();
          case 2 -> option2();
          case 3 -> option3();
          case 4 -> option4();
          case 5 -> option5();
          case 6 -> option6();
          case 7 -> option7();
          case 8 -> option8();
//          case 9 -> option9();
//          case 10 -> option10();
          case 11 -> {
            userDataUtils.close();
            return;
          }
          case 12 -> menuOptions();
          default -> throw new AppException("INPUT OPTION IS NOT DEFINED");
        }
      } catch (AppException e) {
        System.out.println(e.getExceptionMessage());
        System.err.println(Arrays.toString(e.getStackTrace()));
      }
    }
  }


  private void option1() {
    boolean isAdded = entityService.addCustomer();
    if (!isAdded) {
      throw new AppException("Specified customer object couldn't have been added to db");
    }
  }

  private void option2() {

    String jsonFilename = userDataUtils.getString("Input json filename");
    if (jsonFilename == null || !jsonFilename.matches(".+\\.json$")) {
      throw new AppException("Wrong json file format");
    }
    boolean isAdded = entityService.addMovie(jsonFilename);

    if (!isAdded) {
      throw new AppException("Movie raw data couldn't be added to db");
    }

  }

  private void option3() {

    final Integer integer = userDataUtils.getInt("Input customer id you want to delete from database");
    entityService.deleteCustomer(integer);
  }

  private void option4() {
    final Integer integer = userDataUtils.getInt("Input movie id you want to delete from database");
    entityService.deleteMovie(integer);
  }

  private void option5() {
    entityService.showAllCustomers();
  }

  private void option6() {
    entityService.showAllMovies();
  }

  private void option7() {
    final int movieId = userDataUtils.getInt("Input movie id");
    entityService.findMovieById(movieId).ifPresent(System.out::println);
  }

  private void option8() {
    final int customerId = userDataUtils.getInt("Input customer id");
    entityService.findMovieById(customerId).ifPresent(System.out::println);
  }


  private void menuOptions() {

    String menu = MessageFormat.format(
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
                    "Option no. 12  -{11}",

            "Add new Customer",
            "Add new movie",
            "Delete a customer by id",
            "Delete a movie by id",
            "Show all customers",
            "Shows all movies",
            "Show one row from movies",
            "Show one row from customers"

    );
    System.out.println(menu);
  }

}


