package repository.impl;

import connection.DbConnection;
import exceptions.AppException;
import model.Customer;
import org.jdbi.v3.core.Jdbi;
import repository.CrudRepository;


import java.util.List;
import java.util.Optional;

public class CustomerRepository implements CrudRepository<Customer> {

  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  @Override
  public void add(Customer customer) {

    if (customer == null) {
      throw new AppException("customer object is null");
    }

    jdbi.withHandle(handle -> handle
            .createUpdate("insert into customers (name, surname, age, email) values (?, ?, ?, ?)")
            .bind(0, customer.getName())
            .bind(1, customer.getSurname())
            .bind(2, customer.getAge())
            .bind(3, customer.getEmail())
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
                    .createUpdate("update customers set name = ?, surname = ?, age = ?, email = ? where id = ?")
                    .bind(0, customer.getName() == null ? customerFromDb.getName() : customer.getName())
                    .bind(1, customer.getSurname() == null ? customerFromDb.getSurname() : customer.getSurname())
                    .bind(2, customer.getAge() == null ? customerFromDb.getAge() : customer.getAge())
                    .bind(3, customer.getEmail() == null ? customerFromDb.getEmail() : customer.getEmail())
                    .bind(4, customer.getId())
                    .execute()));

  }

  @Override
  public void delete(Integer id) {

    if (id == null) {
      throw new AppException("id is null");
    }

    jdbi.withHandle(handle ->  handle
            .createUpdate("delete from customers where id = :id")
            .bind("id", id)
            .execute());
  }

  @Override
  public Optional<Customer> findById(Integer id) {

    if (id == null) {
      throw new AppException("id is null");
    }

    return jdbi.withHandle(handle -> handle
            .createQuery("select * from customers where id = :id")
            .bind("id", id)
            .mapToBean(Customer.class)
            .findFirst());
  }

  @Override
  public List<Customer> findAll() {
    return jdbi.withHandle(handle -> handle
            .createQuery("select * from customers")
            .mapToBean(Customer.class)
            .list());
  }
}
