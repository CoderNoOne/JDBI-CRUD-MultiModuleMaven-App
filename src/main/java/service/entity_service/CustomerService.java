package service.entity_service;

import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Customer;
import repository.entity_repository.impl.CustomerRepository;
import utils.others.UserDataUtils;
import validators.impl.CustomerValidator;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;

  private Customer createCustomer(String name, String surname, int age, String email) {
    return Customer.builder().name(name).surname(surname).age(age).email(email).build();
  }

  public boolean addCustomer(String name, String surname, int age, String email) {
    Customer customer = createCustomer(name, surname, age, email);
    boolean isValid = new CustomerValidator().validateEntity(customer);

    var customerEmailUnique = isCustomerEmailUnique(customer.getEmail());
    if (isValid && customerEmailUnique) {
      addCustomerToDb(customer);
    }
    return isValid && customerEmailUnique;
  }

  private void addCustomerToDb(Customer customer) {
    customerRepository.add(customer);
  }

  public void deleteCustomer(final Integer id) {
    customerRepository.delete(id);
  }

  public List<Customer> getAllCustomers() {
    return customerRepository.findAll();
  }

  public Optional<Customer> findCustomerById(final Integer id) {
    return customerRepository.findById(id);
  }

  public boolean updateCustomer(Customer customer) {
    boolean isCorrect = new CustomerValidator().validateEntity(customer);
    if (isCorrect) {
      customerRepository.update(customer);
    }
    return isCorrect;
  }

  public Customer getCustomerFromUserInput() {
    getAllCustomers();

    var name = UserDataUtils.getString("Input your name");
    var surname = UserDataUtils.getString("Input your surname");
    var email = UserDataUtils.getString("Input your email");

    return customerRepository.findByNameSurnameAndEmail(name, surname, email).orElseThrow(() -> new AppException(""));
  }

  public Integer findLoyaltyCardIdByCustomerId(Integer customerId) {
    return customerRepository.getLoyaltyCardIdByCustomerId(customerId).orElse(-1);
  }

  public void update(Customer customer) {
    customerRepository.update(customer);
  }

  private boolean isCustomerEmailUnique(String email) {
    return customerRepository.findCustomerByEmail(email).isEmpty();
  }
}
