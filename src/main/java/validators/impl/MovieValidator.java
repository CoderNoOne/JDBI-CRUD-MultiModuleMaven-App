package validators.impl;

import model.Movie;
import validators.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MovieValidator implements Validator<Movie> {

  private Map<String, String> errors = new HashMap<>();

  @Override
  public Map<String, String> validate(Movie movie) {
    errors.clear();

    if (movie == null) {
      errors.put("movie", "movie object is null");
      return errors;
    }

    if (!isDurationValid(movie)) {
      errors.put("Movie Duration", "Movie duration time is less than 0");
    }

    if (!isPriceValid(movie)) {
      errors.put("Movie Price", "Movie Price should be greater than 0");
    }

//    if (!isReleaseDateValid(movie)) {
//      errors.put("Movie Release Date", "Movie release date takes place in the future");
//    }

    return errors;
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public boolean isDurationValid(Movie movie) {
    return movie.getDuration() > 0 && movie.getDuration() < 6;
  }

  public boolean isPriceValid(Movie movie) {
    return movie.getPrice().compareTo(BigDecimal.ZERO) > 0;
  }

//  public boolean isReleaseDateValid(Movie movie) {
//    return movie.getReleaseDate().compareTo(LocalDate.now()) > 0;
//  }

}


