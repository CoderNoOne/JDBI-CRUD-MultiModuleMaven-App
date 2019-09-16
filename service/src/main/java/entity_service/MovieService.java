package entity_service;


import entity.Movie;
import entity_repository.impl.MovieRepository;
import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import validators.impl.MovieValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MovieService {

  private final MovieRepository movieRepository;

  public boolean addMovie(Movie movie) {

    if (movie == null) {
      throw new AppException("Movie is null");
    }

    boolean isValid = new MovieValidator().validateEntity(movie, false);
    if (isValid) {
      movieRepository.add(movie);
    }
    return isValid;
  }

  public void deleteMovie(final Integer id) {

    if (id == null) {
      throw new AppException("Movie id is null");
    }
    movieRepository.delete(id);
  }

  public List<Movie> getAllMovies() {
    return movieRepository.findAll();
  }

  public Optional<Movie> getMovieById(final Integer id) {

    if (id == null) {
      throw new AppException("Movie id is null");
    }
    return movieRepository.findById(id);
  }

  public boolean updateMovie(Movie movie) {

    if (movie == null) {
      throw new AppException("Movie is null");
    }
    boolean isCorrect = new MovieValidator().validateEntity(movie, true);
    if (isCorrect) {
      movieRepository.update(movie);
    }
    return isCorrect;
  }

  public Map<String, Double> getAverageMovieDurationForMovieGenre() {

    return movieRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(
                    Movie::getGenre,
                    Collectors.averagingInt(Movie::getDuration)));
  }

  public Map<String, List<Movie>> getMostExpensiveMovieForEachGenre() {

    List<Movie> allMovies = movieRepository.findAll();

    //noinspection OptionalGetWithoutIsPresent
    return allMovies
            .stream()
            .collect(Collectors.groupingBy(
                    Movie::getGenre,
                    Collectors.maxBy(Comparator.comparing(Movie::getPrice, BigDecimal::compareTo))))
            .entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> allMovies.stream()
                            .filter(movie -> movie.getGenre().equals(e.getKey()) && movie.getPrice().compareTo(e.getValue().get().getPrice()) == 0)
                            .collect(Collectors.toList())));
  }

  public Map<String, List<Movie>> getCheapestMovieForEachGenre() {

    List<Movie> allMovies = movieRepository.findAll();

    //noinspection OptionalGetWithoutIsPresent
    return allMovies
            .stream()
            .collect(Collectors.groupingBy(
                    Movie::getGenre,
                    Collectors.minBy(Comparator.comparing(Movie::getPrice, BigDecimal::compareTo))))
            .entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> allMovies.stream()
                            .filter(movie -> movie.getGenre().equals(e.getKey()) && movie.getPrice().compareTo(e.getValue().get().getPrice()) == 0)
                            .collect(Collectors.toList())));
  }

  public Map<String, Map<LocalDate, List<Movie>>> getTheEarliestPremiereForMovieGenre() {

    List<Movie> allMovies = movieRepository.findAll();

    //noinspection OptionalGetWithoutIsPresent
    return allMovies
            .stream()
            .collect(Collectors.groupingBy(
                    Movie::getGenre,
                    Collectors.minBy(Comparator.comparing(Movie::getReleaseDate, LocalDate::compareTo))))
            .entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> allMovies.stream()
                            .filter(movie -> movie.getGenre().equals(e.getKey()) && movie.getReleaseDate().compareTo(e.getValue().get().getReleaseDate()) == 0)
                            .collect(Collectors.groupingBy(Movie::getReleaseDate))));
  }
}
