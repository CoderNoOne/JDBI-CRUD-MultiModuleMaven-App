package entity_repository.impl;

import connection.DbConnection;
import entity_repository.AbstractCrudRepository;
import exceptions.AppException;
import entity.Customer;
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

public class CustomerRepository extends AbstractCrudRepository<Customer>  {

  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  @Override
  public void add(Customer customer) {

    if (customer == null) {
      throw new AppException("customer object is null");
    }

    jdbi.withHandle(handle -> handle
            .createUpdate("insert into customers (name, surname, age, email) values (:name, :surname, :age, :email)")
            .bind("name", customer.getName())
            .bind("surname", customer.getSurname())
            .bind("age", customer.getAge())
            .bind("email", customer.getEmail())
            .execute());
  }

  @Override
  public void update(Customer customer) {

    if (customer == null) {
      throw new AppException("customer object is null");
    }

    if (customer.getId() == null) {
      throw new AppException("customer id is null");
    }

    jdbi.withHandle(handle -> handle
            .createQuery("select * from customers where id = :id")
            .bind("id", customer.getId())
            .mapToBean(Customer.class)
            .findFirst())
            .ifPresent(customerFromDb -> jdbi.withHandle(handle -> handle
                    .createUpdate("update customers set name = :name, surname = :surname, age = :age, email = :email, loyalty_card_id = :loyaltyCardId where customers.id =:id")
                    .bind("name", customer.getName() == null ? customerFromDb.getName() : customer.getName())
                    .bind("surname", customer.getSurname() == null ? customerFromDb.getSurname() : customer.getSurname())
                    .bind("age", customer.getAge() == null ? customerFromDb.getAge() : customer.getAge())
                    .bind("email", customer.getEmail() == null ? customerFromDb.getEmail() : customer.getEmail())
                    .bind("loyaltyCardId", customer.getLoyaltyCardId() == null ? customerFromDb.getLoyaltyCardId() : customer.getLoyaltyCardId())
                    .bind("id", customer.getId())
                    .execute()));
  }

  public Optional<Customer> findByNameSurnameAndEmail(String name, String surname, String email) {

    if (name == null) {
      throw new AppException("customer name is null");
    }

    if (surname == null) {
      throw new AppException("customer surname is null");
    }

    if (email == null) {
      throw new AppException("customer email is null");
    }
    return jdbi.withHandle(handle -> handle
            .createQuery("select * from customers where name = :name and surname = :surname and email = :email")
            .bind("name", name)
            .bind("surname", surname)
            .bind("email", email)
            .mapToBean(Customer.class)
            .findFirst());
  }

  public Optional<Customer> findCustomerByEmail(String email) {

    return jdbi.withHandle(handle -> handle
            .createQuery("select * from customers where email = :email")
            .bind("email", email)
            .mapToBean(Customer.class)
            .findFirst());
  }

  public Optional<Integer> getLoyaltyCardIdByCustomerId(Integer customerId) {

    return jdbi.withHandle(handle -> handle
            .createQuery("select customers.loyalty_card_id from customers where customers.id =:customerId")
            .bind("customerId", customerId)
            .mapTo(Integer.class)
            .findFirst());
  }
}
