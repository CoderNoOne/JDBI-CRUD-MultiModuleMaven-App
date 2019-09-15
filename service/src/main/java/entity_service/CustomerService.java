
package entity_service;

import entity_repository.impl.CustomerRepository;
import exceptions.AppException;
import entity.Customer;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import validators.impl.CustomerValidator;

@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;

  private Customer createCustomer(String name, String surname, int age, String email) {
    return Customer.builder().name(name).surname(surname).age(age).email(email).build();
  }

  public boolean addCustomer(String name, String surname, Integer age, String email) {

    if (name == null || surname == null || age == null || email == null) {
      throw new AppException("Arguments (name, surname, age, email) cannot be null");
    }

    Customer customer = createCustomer(name, surname, age, email);
    boolean isValid = new CustomerValidator().validateEntity(customer, false);

    if (isValid && isCustomerEmailUnique(customer.getEmail())) {
      addCustomerToDb(customer);
      return true;
    }
    return false;
  }

  private void addCustomerToDb(Customer customer) {

    if (customer == null) {
      throw new AppException("Customer is null");
    }
    customerRepository.add(customer);
  }

  public void deleteCustomer(final Integer id) {

    if (id == null) {
      throw new AppException("Customer id is null");
    }
    customerRepository.delete(id);
  }

  public List<Customer> getAllCustomers() {
    return customerRepository.findAll();
  }

  public Optional<Customer> findCustomerById(final Integer id) {

    if (id == null) {
      throw new AppException("Customer id is null");
    }
    return customerRepository.findById(id);
  }

  public boolean updateCustomer(Customer customer) {

    if (customer == null) {
      throw new AppException("Customer is null");
    }
    boolean isCorrect = new CustomerValidator().validateEntity(customer, true);
    if (isCorrect) {
      customerRepository.update(customer);
    }
    return isCorrect;
  }

  public Customer getCustomerFromUserInput(String name, String surname, String email) {

    if (name == null || surname == null || email == null) {
      throw new AppException(
              String.format("Not valid input arguments (null): %s, %s, %s", name, surname, email));
    }

    return customerRepository
            .findByNameSurnameAndEmail(name, surname, email)
            .orElseThrow(() -> new AppException(String.format("Customer with name: %s, surname %s, email %s is not registered in a db", name, surname, email)));
  }

  private boolean isCustomerEmailUnique(String email) {
    return customerRepository.findCustomerByEmail(email).isEmpty();
  }
}
