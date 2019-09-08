package entity_repository.impl;

import connection.DbConnection;
import entity_repository.AbstractCrudRepository;
import exceptions.AppException;
import entity.SalesStand;
import org.jdbi.v3.core.Jdbi;

public class SalesStandRepository extends AbstractCrudRepository<SalesStand> {

  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  @Override
  public void add(SalesStand salesStand) {

    if (salesStand == null) {
      throw new AppException("salesStand object is null");
    }

    jdbi.withHandle(handle -> handle
            .createUpdate("insert into sales_stands (customer_id, movie_id, start_date_time) values (:customerId, :movieId, :startDateTime)")
            .bind("customerId", salesStand.getCustomerId())
            .bind("movieId", salesStand.getMovieId())
            .bind("startDateTime", salesStand.getStartDateTime())
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
                    .createUpdate("update sales_stands set customer_id = :customerId, movie_id = :movieId, startDateTime = :startDateTime where id = :id")
                    .bind("customerId", salesStand.getCustomerId() == null ? saleStandFromDb.getCustomerId() : salesStand.getCustomerId())
                    .bind("movieId", salesStand.getMovieId() == null ? saleStandFromDb.getMovieId() : salesStand.getMovieId())
                    .bind("startDateTime", salesStand.getStartDateTime() == null ? saleStandFromDb.getStartDateTime() : salesStand.getStartDateTime())
                    .bind("id", salesStand.getId())
                    .execute()));
  }
}

