package utils;

import java.text.MessageFormat;

public class MenuOptionsUtils {

  private MenuOptionsUtils() {
  }

  public static void mainMenuOptions() {

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

  public static void customerAndMovieTableManagmentMenu() {
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
            "Show all customers",
            "Shows all movies bought by specified customer",
            "Show customer detail by his email",
            "Show movie detail by title",
            "Tickets bought in the specified time interval",
            "Tickets bought by specified customer and in the specified time interval",
            "Show movies that lasts in the specified time range",
            "Update customer",
            "Update movie",
            "Back to main menu",
            "Update movie"));
  }

  public void transactionHistoryMenu() {
    System.out.println(MessageFormat.format(
            "\nOption no. 1 - {0}\n" +
                    "Option no. 2 - {1}\n" +
                    "Option no. 3 - {2}\n" +
                    "Option no. 4 - {3}\n" +
                    "Option no. 5 - {4}",

            "All movie ticket bought",
            "ticket bought in the time within",
            "movies that lasts x hours",
            "",
            ""
    ));

  }


  public static void statisticsOptionsMenu() {

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

            "Movies grouped by the most popular ones",
            "Most popular movie genre grouped by each customer ",
            "The most expensive ticket bought grouped each customer",
            "The cheapest ticket bought grouped for each customer",
            "Average ticket price grouped by month",
            "Total monthly expenses on tickets grouped by month for each customer",
            "Total amount of tickets bought with discount by movie category and grouped by each customer",
            "Total amount of tickets bought without discount by movie category and grouped by each customer",
            "Back to main menu"

    ));
  }
}
