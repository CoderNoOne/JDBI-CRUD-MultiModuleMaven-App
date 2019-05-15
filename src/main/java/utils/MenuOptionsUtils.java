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
                    "Option no. 9 - {8}\n" +
                    "Option no. 10 - {9}\n" +
                    "Option no. 11 - {10}\n" +
                    "Option no. 12  -{11}\n" +
                    "Option no. 13 - {12}\n" +
                    "Option no. 14 - {13}",

            "Add new Customer",
            "Add new movie",
            "Delete a customer by id",
            "Delete a movie by id",
            "Show all customers",
            "Shows all movies",
            "Show one row from movies",
            "Show one row from customers",
            "Generate example data for table movies and customers",
            "Buy a ticket",
            "History - summary",
            "Some statistics",
            "Show menu options"

    ));
  }

  public static void option3Menu() {
    System.out.println(MessageFormat.format(
            "\nOption no. 1 - {0}\n" +
                    "Option no. 2 - {1}\n" +
                    "Option no. 3 - {2}\n" +
                    "Option no. 4 - {3}\n" +
                    "Option no. 5 - {4}\n" +
                    "Option no. 6 - {5}\n" +
                    "Option no. 7 - {6}\n" +
                    "Option no. 8 - {7}",

            "Delete customer",
            "Delete movie",
            "Show all customers",
            "Shows all movies",
            "Show customer by id",
            "Show movie by id",
            "Update customer",
            "Update movie"
    ));
  }

  public void option11Menu() {
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
}
