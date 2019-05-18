package repository.others;

import connection.DbConnection;
import model.others.CustomerWithLoyaltyCard;
import model.others.CustomerWithMoviesAndSalesStand;
import model.others.MovieWithSalesStand;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Optional;

public class JoinedEntitiesRepository {

  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  public List<MovieWithSalesStand> getMovieWithSalesStand() {

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

  public List<CustomerWithMoviesAndSalesStand> getCustomerWithMoviesAndSalesStand() {

    final String sql = "select movies.genre m_genre, customers.id c_id from movies join sales_stands on movies.id = sales_stands.movie_id join customers on customers.id = sales_stands.customer_id";

    return jdbi.withHandle(handle ->
            handle
                    .createQuery(sql)
                    .map((rs, ctx) -> CustomerWithMoviesAndSalesStand.builder()
                            .customerId(rs.getInt("c_id"))
                            .movieGenre(rs.getString("m_genre"))
                            .build())
                    .list());
  }

  public List<CustomerWithMoviesAndSalesStand> getAllTicketsByCustomerId(Integer id) {

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


}
