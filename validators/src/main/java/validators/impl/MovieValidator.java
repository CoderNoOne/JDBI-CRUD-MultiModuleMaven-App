package validators.impl;

import entity.Movie;
import validators.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static others.UserDataUtils.printMessage;

public class MovieValidator implements Validator<Movie> {

  private Map<String, String> errors = new HashMap<>();

  @Override
  public Map<String, String> validate(Movie movie, boolean isUpdate) {
    errors.clear();

    if (movie == null) {
      errors.put("movie", "movie object is null");
      return errors;
    }

    if (!isDurationValid(movie, isUpdate)) {
      errors.put(
              "Movie Duration",
              !isUpdate ?
                      "Movie duration should be greater than 0 and less than 6"
                      : "Movie duration should remain null or be in the range (0,6)");
    }

    if (!isPriceValid(movie, isUpdate)) {
      errors.put(
              "Movie Price",
              !isUpdate ?
                      "Movie Price should be greater than 0"
                      : "Movie Price should remain null or be greater than 0");
    }

    if (!isReleaseDateValid(movie, isUpdate)) {
      errors.put(
              "Movie Release Date",
              !isUpdate ?
                      "Movie release date should take place in the future"
                      : "Movie release date should remain null or take place in the future");
    }
    if (!isTitleValid(movie, isUpdate)) {
      errors.put(
              "Movie title",
              !isUpdate ?
                      "Movie title should consists of capital letters and space between them"
                      : "Movie title should remain null or should consists of capital letters and space between them");
    }

    if (!isGenreValid(movie, isUpdate)) {
      errors.put(
              "Movie genre",
              !isUpdate ? "Movie genre should contain only capital letters and space between them"
                      : "Movie genre should remain null or contain only capital letters and space between them"
      );

    }

    return errors;
  }

  private boolean isGenreValid(Movie movie, boolean isUpdate) {
    return !isUpdate ? movie.getGenre() != null && movie.getGenre().matches("([A-Z]+[\\s]*)*([A-Z]+)$") :
            movie.getGenre() == null || movie.getGenre().matches("([A-Z]+[\\s]*)*([A-Z]+)$");
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public boolean validateEntity(Movie movie, boolean isUpdate) {
    validate(movie, isUpdate);

    if (hasErrors()) {
      printMessage(errors
              .entrySet()
              .stream()
              .map(e -> e.getKey() + " : " + e.getValue())
              .collect(Collectors.joining("\n")));
    }
    return !hasErrors();
  }

  private boolean isTitleValid(Movie movie, boolean isUpdate) {

    return !isUpdate ?
            movie.getTitle() != null && movie.getTitle().matches("([A-Z]+[\\s]*)*([A-Z]+)$")
            : movie.getTitle() == null || movie.getTitle().matches("([A-Z]+[\\s]*)*([A-Z]+)$");
  }

  private boolean isDurationValid(Movie movie, boolean isUpdate) {
    return !isUpdate ?
            movie.getDuration() != null && movie.getDuration() > 0 && movie.getDuration() < 6
            : movie.getDuration() == null || (movie.getDuration() > 0 && movie.getDuration() < 6);
  }

  private boolean isPriceValid(Movie movie, boolean isUpdate) {
    return !isUpdate ?
            movie.getPrice() != null && movie.getPrice().compareTo(BigDecimal.ZERO) > 0
            : movie.getPrice() == null || movie.getPrice().compareTo(BigDecimal.ZERO) > 0;
  }

  private boolean isReleaseDateValid(Movie movie, boolean isUpdate) {
    return !isUpdate ?
            movie.getReleaseDate() != null && movie.getReleaseDate().compareTo(LocalDate.now()) > 0
            : movie.getReleaseDate() == null || movie.getReleaseDate().compareTo(LocalDate.now()) > 0;
  }
}


