package repository.impl;

import connection.DbConnection;
import exceptions.AppException;
import model.SalesStand;
import org.jdbi.v3.core.Jdbi;
import repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public class SalesStandRepository implements CrudRepository<SalesStand> {

  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  @Override
  public void add(SalesStand salesStand) {

    if (salesStand == null) {
      throw new AppException("salesStand object is null");
    }

    jdbi.withHandle(handle -> handle
            .createUpdate("insert into sales_stands (customer_id, movie_id, start_date_time) values (?, ?, ?)")
            .bind(0, salesStand.getCustomerId())
            .bind(1, salesStand.getMovieId())
            .bind(2, salesStand.getStartDateTime())
            .execute());
  }

  @Override
  public void update(SalesStand salesStand) {

    if (salesStand == null) {
      throw new AppException("salesStand object is null");
    }

    if (salesStand.getId() == null) {
      throw new AppException("Sales stand id is null");
    }

    jdbi.withHandle(handle -> handle
            .createQuery("select * from sales_stands where id = :id")
            .bind("id", salesStand.getId())
            .mapToBean(SalesStand.class)
            .findFirst())
            .ifPresent(saleStandFromDb -> jdbi.withHandle(handle -> handle
                    .createUpdate("update sales_stands set customer_id = ?, movie_id = ?, start_date_time = ? where id = ?")
                    .bind(0, salesStand.getCustomerId() == null ? saleStandFromDb.getCustomerId() : salesStand.getCustomerId())
                    .bind(1, salesStand.getMovieId() == null ? saleStandFromDb.getMovieId() : salesStand.getMovieId())
                    .bind(2, salesStand.getStartDateTime() == null ? saleStandFromDb.getStartDateTime() : salesStand.getStartDateTime())
                    .execute()));

  }

  @Override
  public void delete(Integer id) {

    if (id == null) {
      throw new AppException("id is null");
    }

    jdbi.withHandle(handle -> handle
            .createUpdate("delete from sales_stands where id = :id")
            .bind("id", id)
            .execute());
  }

  @Override
  public Optional<SalesStand> findById(Integer id) {

    if (id == null) {
      throw new AppException("id is null");
    }

    return jdbi.withHandle(handle -> handle
            .createQuery("select * from sales_stands where id = :id")
            .bind("id", id)
            .mapToBean(SalesStand.class)
            .findFirst());
  }

  @Override
  public List<SalesStand> findAll() {
    return jdbi.withHandle(handle -> handle
            .createQuery("select * from sales_stand")
            .mapToBean(SalesStand.class)
            .list());
  }

  @Override
  public void deleteAll() {
    jdbi.withHandle(handle -> handle
            .createUpdate("delete from sales_stands")
            .execute());
  }

  public Integer ticketsNumberBoughtByCustomerId(Integer customerId) {

    return jdbi.withHandle(handle -> handle
            .select("select count(*) from sales_stands where customer_id = ?", customerId)
            .mapTo(Integer.class)
            .findOnly());

  }
}
