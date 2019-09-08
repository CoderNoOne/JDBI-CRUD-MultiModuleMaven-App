package others;

import connection.DbConnection;
import exceptions.AppException;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;

public class JoinedEntitiesRepository {

  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  public List<MovieWithSalesStand> getAllMovieWithSalesStand() {

    final String sql = "select movies.id m_id, movies.title m_title, movies.genre m_genre, movies.price m_price, movies.duration m_duration, movies.release_date m_release_date from movies join sales_stands on movies.id = sales_stands.movie_id";

    return jdbi.withHandle(handle ->
            handle
                    .createQuery(sql)
                    .map((rs, ctx) -> MovieWithSalesStand.builder()
                            .movieId(rs.getInt("m_id"))
                            .movieTitle(rs.getString("m_title"))
                            .movieGenre(rs.getString("m_genre"))
                            .movieDuration(rs.getInt("m_duration"))
                            .moviePrice(rs.getBigDecimal("m_price"))
                            .movieReleaseDate(rs.getDate("m_release_date").toLocalDate())
                            .build())
                    .list());
  }

  public List<CustomerWithMoviesAndSalesStand> getAllCustomerWithMoviesAndSalesStand() {

    final String sql = "select customers.id c_id, customers.name c_name, customers.surname c_surname, customers.age c_age, customers.email c_email, customers.loyalty_card_id c_loyalty_card_id, movies.genre m_genre, movies.duration m_duration, movies.title m_title, movies.price m_price, movies.release_date m_release_date from movies join sales_stands on movies.id = sales_stands.movie_id join customers on customers.id = sales_stands.customer_id";

    return jdbi.withHandle(handle ->
            handle
                    .createQuery(sql)
                    .map((rs, ctx) -> CustomerWithMoviesAndSalesStand.builder()
                            .customerId(rs.getInt("c_id"))
                            .customerName(rs.getString("c_name"))
                            .customerSurname(rs.getString("c_surname"))
                            .customerAge(rs.getInt("c_age"))
                            .customerEmail(rs.getString("c_email"))
                            .customerLoyaltyCardId(rs.getInt("c_loyalty_card_id"))
                            .movieDuration(rs.getInt("m_price"))
                            .movieGenre(rs.getString("m_genre"))
                            .movieTitle(rs.getString("m_title"))
                            .movieReleaseDate(rs.getDate("m_release_date").toLocalDate())
                            .build())
                    .list());
  }

  public List<CustomerWithMoviesAndSalesStand> getAllTicketsByCustomerId(Integer id) {

    if (id == null) {
      throw new AppException("Id is null");
    }

    final String sql = "select movies.title m_title, movies.genre m_genre, movies.price m_price, movies.duration m_duration, movies.release_date m_releaseDate," +
            " sales_stands.start_date_time as s_startTime from sales_stands join customers on sales_stands.customer_id = customers.id join movies on movies.id = sales_stands.movie_id where sales_stands.customer_id = :customerId";

    return jdbi.withHandle(handle ->
            handle
                    .createQuery(sql)
                    .bind("customerId", id)
                    .map((rs, ctx) -> CustomerWithMoviesAndSalesStand.builder()
                            .customerId(id)
                            .movieTitle(rs.getString("m_title"))
                            .movieDuration(rs.getInt("m_duration"))
                            .movieGenre(rs.getString("m_genre"))
                            .movieReleaseDate(rs.getDate("m_releaseDate").toLocalDate())
                            .ticketPrice(rs.getBigDecimal("m_price"))
                            .startDateTime(rs.getTimestamp("s_startTime").toLocalDateTime())
                            .build()).list());
  }

  public Optional<CustomerWithLoyaltyCard> getCustomerWithLoyaltyCardInfoByCustomerId(Integer customerId) {

    if (customerId == null) {
      throw new AppException("Id is null");
    }
    final String sql = String.join(" ", "select customers.id c_id, loyalty_cards.movies_number lc_movie_numbers,"
            , "loyalty_cards.discount lc_discount, loyalty_cards.expiration_date lc_exp_date, loyalty_cards.id lc_id"
            , "from customers join loyalty_cards on customers.loyalty_card_id = loyalty_cards.id where customers.id =:customerId");

    return jdbi.withHandle(handle ->
            handle
                    .createQuery(sql)
                    .bind("customerId", customerId)
                    .map((rs, ctx) -> CustomerWithLoyaltyCard.builder()
                            .customerId(rs.getInt("c_id"))
                            .discount(rs.getBigDecimal("lc_discount"))
                            .moviesNumber(rs.getInt("lc_movie_numbers"))
                            .loyaltyCardExpirationDate(rs.getDate("lc_exp_date").toLocalDate())
                            .loyaltyCardId(rs.getInt("lc_id"))
                            .build()).findFirst());
  }

  public List<CustomerWithLoyaltyCard> getAllCustomerWithLoyaltyCard() {

    final String sql = "select customers.id c_id, loyalty_cards.movies_number lc_mn, loyalty_cards.discount lc_discount, loyalty_cards.expiration_date lc_exp_date, loyalty_cards.id lc_id from customers join loyalty_cards on customers.loyalty_card_id = loyalty_cards.id";

    return jdbi.withHandle(handle ->
            handle
                    .createQuery(sql)
                    .map((rs, ctx) -> CustomerWithLoyaltyCard.builder()
                            .customerId(rs.getInt("c_id"))
                            .loyaltyCardId(rs.getInt("lc_id")).loyaltyCardExpirationDate(rs.getDate("lc_exp_date").toLocalDate())
                            .moviesNumber(rs.getInt("lc_mn"))
                            .discount(rs.getBigDecimal("lc_discount"))
                            .build()).list());
  }
}

