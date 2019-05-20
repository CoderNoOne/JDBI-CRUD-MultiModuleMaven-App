package utils.update;

import exceptions.AppException;
import model.enums.CustomerField;
import model.entity.Customer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static utils.others.UserDataUtils.*;

public class UpdateCustomerUtils {

  private UpdateCustomerUtils() {
  }

  public static Customer getUpdatedCustomer(Customer customer) {

    List<CustomerField> customerFields = Arrays.stream(CustomerField.values()).collect(Collectors.toList());

    boolean hasNext;
    do {
      printCollectionWithNumeration(customerFields);
      CustomerField customerProperty = CustomerField.valueOf(getString("Choose what customer property you want to update. Not case sensitive").toUpperCase());

      switch (customerProperty) {
        case NAME -> {
          String updatedName = getString("Type customer new name");
          customerFields.remove(CustomerField.NAME);
          customer.setName(updatedName);
        }
        case SURNAME -> {
          String updatedSurname = getString("Type customer new surname");
          customerFields.remove(CustomerField.SURNAME);
          customer.setSurname(updatedSurname);
        }
        case AGE -> {
          int updatedAge = getInt("Type customer new age");
          customerFields.remove(CustomerField.AGE);
          customer.setAge(updatedAge);
        }
        case EMAIL -> {
          String updatedEmail = getString("Type customer new email");
          customerFields.remove(CustomerField.EMAIL);
          customer.setEmail(updatedEmail);
        }
        default -> throw new AppException("Not valid customer property");
      }
      hasNext = getString("Do you want to update other customer property? (Y/N)").equalsIgnoreCase("Y");
    } while (hasNext && !customerFields.isEmpty());

    return customer;
  }
}


