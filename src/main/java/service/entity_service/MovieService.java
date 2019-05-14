package service.entity_service;

import converters.impl.MovieJsonConverter;
import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Movie;
import repository.impl.MovieRepository;
import validators.impl.MovieValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
public class MovieService {

  private final MovieRepository movieRepository;

  private Movie createMovie(final String jsonfileName) {

    return new MovieJsonConverter(jsonfileName)
            .fromJson()
            .orElseThrow(() -> new AppException("FILE " + jsonfileName + " is empty"));
  }

  public boolean addMovie(final String jsonfileName) {
    Movie movie = createMovie(jsonfileName);
    boolean isValid = new MovieValidator().validateEntity(movie);
    if (isValid) {
      movieRepository.add(movie);
    }
    return isValid;
  }

  public void deleteMovie(final Integer id) {
    movieRepository.delete(id);
  }

  public void showAllMovies() {
    movieRepository.findAll().forEach(System.out::println);
  }

  public Optional<Movie> findMovieById(final Integer id) {
    return movieRepository.findById(id);
  }

  private void updateMovie(Movie movie) {
    movieRepository.update(movie);
  }

  public boolean updateMovieDetail(Integer id, String title, String genre, LocalDate releaseDate, Integer duration, BigDecimal price) {

    Movie movie = Movie.builder().id(id).title(title).genre(genre).price(price).duration(duration).releaseDate(releaseDate).build();

    boolean isValid = new MovieValidator().validateEntity(movie);

    if (isValid) {
      updateMovie(movie);
    }
    return isValid;
  }
}
