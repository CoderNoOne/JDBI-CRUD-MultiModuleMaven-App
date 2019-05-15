package repository.impl;

import connection.DbConnection;
import exceptions.AppException;
import model.entity.Customer;
import model.others.CustomerWithMoviesAndSalesStand;
import model.entity.SalesStand;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
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

  public List<CustomerWithMoviesAndSalesStand> getAllTicketsByCustomerId(Integer id) {

    final String sql = "select movies.title m_title, movies.genre m_genre, movies.price m_price, movies.duration m_duration, movies.release_date m_releaseDate," +
            " sales_stands.start_date_time as s_startTime from sales_stands join customers on sales_stands.customer_id = customers.id join movies on movies.id = sales_stands.movie_id where sales_stands.customer_id = :customerId";

    return jdbi.withHandle(handle ->
            handle
                    .createQuery(sql)
                    .bind("customerId", id)
                    .map((rs, ctx) -> CustomerWithMoviesAndSalesStand.builder()
                            .id(id)
                            .movieTitle(rs.getString("m_title"))
                            .movieDuration(rs.getInt("m_duration"))
                            .movieGenre(rs.getString("m_genre"))
                            .movieReleaseDate(rs.getDate("m_releaseDate").toLocalDate())
                            .ticketPrice(rs.getBigDecimal("m_price"))
                            .startDateTime(rs.getTimestamp("s_startTime").toLocalDateTime())
                            .build()).list());
  }

  public String getCustomerEmailByCustomerId(Integer id) {
    return jdbi.withHandle(handle -> handle.createQuery("select customers.email from sales_stands join customers on sales_stands.customer_id = customers.id where sales_stands.customer_id = :customerId")
            .bind("customerId", id)
            .mapTo(String.class)
            .findFirst().orElseThrow());
  }
}

