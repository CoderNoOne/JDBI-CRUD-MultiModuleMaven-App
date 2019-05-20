package utils.update;

import exceptions.AppException;
import model.enums.MovieField;
import model.entity.Movie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static utils.others.UserDataUtils.*;

public class UpdateMovieUtils {

  private UpdateMovieUtils() {
  }

  public static Movie getUpdatedMovie(Movie movie) {

    List<MovieField> movieFields = Arrays.stream(MovieField.values()).collect(Collectors.toList());

    boolean hasNext;
    do {
      printCollectionWithNumeration(movieFields);
      MovieField movieProperty = MovieField.valueOf(getString("Choose what movie property you want to update. Not case sensitive").toUpperCase());

      switch (movieProperty) {
        case TITLE -> {
          String updatedTitle = getString("Type movie new title");
          movieFields.remove(MovieField.TITLE);
          movie.setTitle(updatedTitle);
        }
        case GENRE -> {
          String updatedGenre = getString("Type movie new genre");
          movieFields.remove(MovieField.GENRE);
          movie.setGenre(updatedGenre);
        }
        case DURATION -> {
          Integer updatedDuration = getInt("Type movie new duration");
          movieFields.remove(MovieField.DURATION);
          movie.setDuration(updatedDuration);
        }
        case PRICE -> {
          BigDecimal updatedPrice = getBigDecimal("Type movie new price");
          movieFields.remove(MovieField.PRICE);
          movie.setPrice(updatedPrice);
        }
        case RELEASE_DATE -> {
          LocalDate updatedReleaseDate = getLocalDate("Type movie new release date");
          movieFields.remove(MovieField.RELEASE_DATE);
          movie.setReleaseDate(updatedReleaseDate);
        }
        default -> throw new AppException("Not valid movie property");
      }
      hasNext = getString("Do you want to update other movie property (Y/N)").equalsIgnoreCase("Y");
    } while (hasNext && !movieFields.isEmpty());
    return movie;
  }

}
