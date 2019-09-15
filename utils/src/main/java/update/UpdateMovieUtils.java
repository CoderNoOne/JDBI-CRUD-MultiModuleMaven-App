package update;

import entity.Movie;

import static others.UserDataUtils.*;


public interface UpdateMovieUtils {

  static Movie getUpdatedMovie(Integer id) {

    return Movie.builder()
            .id(id)
            .title(getString("Do you want to update movie title ?").equalsIgnoreCase("Y") ? getString("Input movie new title") : null)
            .genre(getString("Do you want to update movie genre ?").equalsIgnoreCase("Y") ? getString("Input movie new genre") : null)
            .duration(getString("Do you want to update movie duration ?").equalsIgnoreCase("Y") ? getInt("Input movie new duration") : null)
            .releaseDate(getString("Do you want to update movie release date ?").equalsIgnoreCase("Y") ? getLocalDate("Input movie new release date") : null)
            .price(getString("Do you want to update movie price ?").equalsIgnoreCase("Y") ? getBigDecimal("Input movie new price") : null)
            .build();

  }

}
