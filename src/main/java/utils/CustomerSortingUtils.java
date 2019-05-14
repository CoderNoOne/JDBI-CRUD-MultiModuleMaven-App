package utils;

import exceptions.AppException;
import model.sorting.sorting_comparator.CustomerSort;
import model.sorting.sortingCriterion.CustomerSortingCriterion;

import java.util.Arrays;
import java.util.List;

import static model.sorting.sortingCriterion.CustomerSortingCriterion.*;
import static utils.UserDataUtils.getString;

public class CustomerSortingUtils {

  private static CustomerSort.CustomerSortBuilder builder;
  private static List<CustomerSortingCriterion> sortingAlgorithms;

  private CustomerSortingUtils() {
  }

  public static CustomerSort getCustomerSortingAlgorithm(String message) {

    sortingAlgorithms = Arrays.asList(CustomerSortingCriterion.values());
    builder = new CustomerSort.CustomerSortBuilder();
    System.out.println(message);

    while (true) {

      CustomerSortingCriterion sortingCriterion = CustomerSortingCriterion.valueOf(getString("CHOOSE FROM ABOVE: " + sortingAlgorithms).toUpperCase());

      if (sortingAlgorithms.isEmpty() || !sortingAlgorithms.contains(sortingCriterion)) throw new AppException("UNDEFINED SORTING CRITERION OR ALREADY SORTED BY THIS ONE");

      switch (sortingCriterion) {
        case NAME -> sortByName();
        case SURNAME -> sortBySurname();
        case AGE -> sortByAge();
        case EMAIL -> sortByEmail();
      }
      if (!getString("DO YOU WANT TO ADD NEW SORTING CRITERION? Y/N").equalsIgnoreCase("Y")) break;
    }
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

