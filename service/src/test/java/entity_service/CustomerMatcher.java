package entity_service;

import com.google.gson.internal.$Gson$Preconditions;
import entity.Customer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerMatcher extends TypeSafeMatcher<Customer> {

  private final String name;
  private final String surname;
  private final Integer age;
  private final String email;

  private boolean isCustomerOk = true;
  private boolean isNameOk = true;
  private boolean isSurnameOk = true;
  private boolean isAgeOk = true;
  private boolean isEmailOk = true;

  @Override
  protected boolean matchesSafely(Customer customer) {

    if (customer == null) {
      isCustomerOk = false;
      return false;
    }

    if (!Objects.equals(customer.getName(), name)) {
      isNameOk = false;
    }

    if (!Objects.equals(customer.getSurname(), surname)) {
      isSurnameOk = false;
    }

    if (!Objects.equals(customer.getAge(), age)) {
      return false;
    }

    if (!Objects.equals(customer.getEmail(), email)) {
      return false;
    }

    return isNameOk && isSurnameOk && isAgeOk && isEmailOk;
  }

  @Override
  public void describeTo(Description description) {

    if (!isCustomerOk) {
      description.appendText("customer is null");
    }

    if (!isNameOk) {
      description.appendText("customer name is not valid");
    }

    if (!isSurnameOk) {
      description.appendText("customer surname is not valid");
    }

    if (!isAgeOk) {
      description.appendText("customer age is not valid");
    }
  }

  public static CustomerMatcher matches(final String name, final String surname, final Integer age, final String email) {

    return new CustomerMatcher(name, surname, age, email);
  }

}
