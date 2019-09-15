package validators.impl;


import validators.Validator;
import entity.Customer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static others.UserDataUtils.printMessage;

public class CustomerValidator implements Validator<Customer> {

  private Map<String, String> errors = new HashMap<>();

  @Override
  public Map<String, String> validate(Customer customer, boolean isUpdate) {

    if (customer == null) {
      errors.put("customer", "customer object is null");
      return errors;
    }

    if (!isAgeValid(customer, isUpdate)) {
      errors.put(
              "Customer age",
              !isUpdate ?
                      "Customer should be adult"
                      : "Customer age should remain null or be greater than 18");
    }

    if (!isNameValid(customer, isUpdate)) {
      errors.put(
              "Customer name",
              !isUpdate ?
                      "Customer name should contain only capital letters and whitespaces"
                      : "Customer name should remain null or it should contain only capital letters and whitespaces");
    }


    if (!isSurnameValid(customer, isUpdate)) {
      errors.put(
              "Customer surname",
              !isUpdate ?
                      "Customer surname should contain only capital letters and whitespaces"
                      : "Customer surname should remain null or contain only capital letters and whitespaces");
    }

    if (!isEmailValid(customer, isUpdate)) {
      errors.put(
              "Customer email",
              !isUpdate ? "Customer email is not valid"
                      : "Customer email should remain null or it should be valid");
    }

    return errors;
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public boolean validateEntity(Customer customer, boolean isUpdate) {

    validate(customer, isUpdate);

    if (hasErrors()) {
      printMessage(errors
              .entrySet()
              .stream()
              .map(e -> e.getKey() + " : " + e.getValue())
              .collect(Collectors.joining("\n")));
    }
    return !hasErrors();
  }

  private boolean isAgeValid(Customer customer, boolean isUpdate) {
    return !isUpdate ? customer.getAge() != null && customer.getAge() >= 18 : customer.getAge() == null || customer.getAge() >= 18;
  }

  private boolean isSurnameValid(Customer customer, boolean isUpdate) {
    return !isUpdate ?
            customer.getSurname() != null && customer.getSurname().matches("[A-Z]+[\\s]*[A-Z]*")
            : customer.getSurname() == null || customer.getSurname().matches("[A-Z]+[\\s]*[A-Z]*");
  }

  private boolean isNameValid(Customer customer, boolean isUpdate) {
    return !isUpdate ?
            customer.getName() != null && customer.getName().matches("[A-Z]+[\\s]*[A-Z]*")
            : customer.getName() == null || customer.getName().matches("[A-Z]+[\\s]*[A-Z]*");
  }

  private boolean isEmailValid(Customer customer, boolean isUpdate) {
    return !isUpdate ? customer.getEmail() != null && customer.getEmail().matches("^[\\w.+\\-]+@gmail\\.com$")
            : customer.getEmail() == null || customer.getEmail().matches("^[\\w.+\\-]+@gmail\\.com$");
  }
}
