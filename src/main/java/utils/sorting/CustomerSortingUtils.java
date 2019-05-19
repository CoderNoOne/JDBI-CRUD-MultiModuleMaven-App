package utils.sorting;

import exceptions.AppException;
import model.sorting.sorting_comparator.CustomerSort;
import model.entities_fields.CustomerField;

import java.util.Arrays;
import java.util.List;

import static model.entities_fields.CustomerField.*;
import static utils.others.UserDataUtils.getString;

public class CustomerSortingUtils {

  private static CustomerSort.CustomerSortBuilder builder;
  private static List<CustomerField> sortedFields;

  private CustomerSortingUtils() {
  }

  public static CustomerSort getCustomerSortingAlgorithm(String message) {

    sortedFields = Arrays.asList(CustomerField.values());
    builder = new CustomerSort.CustomerSortBuilder();
    System.out.println(message);

    while (true) {

      CustomerField sortingCriterion = CustomerField.valueOf(getString("CHOOSE FROM ABOVE: " + sortedFields).toUpperCase());

      if (sortedFields.isEmpty() || !sortedFields.contains(sortingCriterion)) throw new AppException("UNDEFINED SORTING CRITERION OR ALREADY SORTED BY THIS ONE");

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
    sortedFields.remove(EMAIL);
  }

  private static void sortByAge() {
    builder = chooseOrdering().equals("ASC") ? builder.age(true) : builder.age(false);
    sortedFields.remove(AGE);
  }

  private static void sortBySurname() {
    builder = chooseOrdering().equals("ASC") ? builder.surname(true) : builder.surname(false);
    sortedFields.remove(SURNAME);
  }

  private static void sortByName() {
    builder = chooseOrdering().equals("ASC") ? builder.name(true) : builder.name(false);
    sortedFields.remove(NAME);
  }

  private static String chooseOrdering() {
    return getString("CHOOSE ORDERING. ASC/DESC").toUpperCase();
  }
}

