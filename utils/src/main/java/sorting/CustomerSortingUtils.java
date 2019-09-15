package sorting;

import enums.CustomerField;
import sorting.sorting_comparator.CustomerSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static enums.CustomerField.*;
import static others.UserDataUtils.getString;

public final class CustomerSortingUtils {

  private static CustomerSort.CustomerSortBuilder builder;
  private static List<CustomerField> sortingAlgorithms;

  private CustomerSortingUtils() {
  }

  public static CustomerSort getCustomerSortingAlgorithm(String message) {

    sortingAlgorithms = new ArrayList<>(Arrays.asList(CustomerField.values()));
    builder = new CustomerSort.CustomerSortBuilder();
    System.out.println(message);

    boolean hasNext;

    do {

      CustomerField sortingCriterion;

      do {
        sortingCriterion = fromString(getString("CHOOSE FROM ABOVE: " + sortingAlgorithms).toUpperCase());
      } while (sortingCriterion == null);

      switch (sortingCriterion) {
        case NAME -> sortByName();
        case SURNAME -> sortBySurname();
        case AGE -> sortByAge();
        case EMAIL -> sortByEmail();
      }
      hasNext = getString("DO YOU WANT TO ADD NEW SORTING CRITERION? Y/N").equalsIgnoreCase("Y");
    } while (hasNext && !sortingAlgorithms.isEmpty());
    return builder.build();
  }

  private static void sortByEmail() {
    builder = chooseOrdering().equals("ASC") ? builder.email(true) : builder.email(false);
    sortingAlgorithms.remove(EMAIL);
  }

  private static void sortByAge() {
    builder = chooseOrdering().equals("ASC") ? builder.age(true) : builder.age(false);
    sortingAlgorithms.remove(AGE);
  }

  private static void sortBySurname() {
    builder = chooseOrdering().equals("ASC") ? builder.surname(true) : builder.surname(false);
    sortingAlgorithms.remove(SURNAME);
  }

  private static void sortByName() {
    builder = chooseOrdering().equals("ASC") ? builder.name(true) : builder.name(false);
    sortingAlgorithms.remove(NAME);
  }

  private static String chooseOrdering() {
    return getString("CHOOSE ORDERING. ASC/DESC").toUpperCase();
  }
}

