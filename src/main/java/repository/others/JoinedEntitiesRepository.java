package repository.others;

import connection.DbConnection;
import model.entity.Movie;
import model.others.MovieWithSalesStand;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JoinedEntitiesRepository {

  private Jdbi jdbi = DbConnection.getInstance().getJdbi();

  public Map<Movie, Integer> movieGroupedByPopularity() {

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
                    .stream().collect(Collectors.groupingBy(JoinedEntitiesRepository::convertMovieWithSalesStandToMovie, Collectors.summingInt(e -> 1))));
  }

  private static Movie convertMovieWithSalesStandToMovie(MovieWithSalesStand movieWithSalesStand) {
    return Movie.builder()
            .id(movieWithSalesStand.getMovieId())
            .title(movieWithSalesStand.getMovieTitle())
            .genre(movieWithSalesStand.getMovieGenre())
            .price(movieWithSalesStand.getMoviePrice())
            .duration(movieWithSalesStand.getMovieDuration())
            .releaseDate(movieWithSalesStand.getMovieReleaseDate())
            .build();
  }


}

