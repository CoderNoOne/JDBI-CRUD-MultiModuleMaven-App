package validators;

import model.Customer;

import java.util.HashMap;
import java.util.Map;

public class CustomerValidator implements Validator<Customer> {

  private Map<String, String> errors = new HashMap<>();

  @Override
  public Map<String, String> validate(Customer customer) {

    if (customer == null) {
      errors.put("customer", "customer object is null");
      return errors;
    }

    if (!isAgeValid(customer)) {
      errors.put("Customer age", "customer is not adult");
    }

    if (!isNameValid(customer)) {
      errors.put("Customer name", "Customer name should contain only capital letters and whitespaces");
    }


    if (!isSurnameValid(customer)) {
      errors.put("Customer surname", "Customer surnname should contain only capital letters and whitespaces");
    }

    if (!isEmailValid(customer)) {
      errors.put("Customer email", "Customer email is not valid");
    }

    return errors;
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  private boolean isAgeValid(Customer customer) {
    return customer.getAge() >= 18;
  }

  private boolean isSurnameValid(Customer customer) {
    return customer.getSurname().matches("[A-Z]+[\\s]*[A-Z]*");
  }

  private boolean isNameValid(Customer customer) {
    return customer.getName().matches("[A-Z]+[\\s]*[A-Z]*");
  }

  private boolean isEmailValid(Customer customer) {

    return new MailValidator(false).isValid(customer.getEmail());

  }
}