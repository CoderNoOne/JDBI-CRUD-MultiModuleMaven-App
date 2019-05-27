package service.entity_service;

import converters.impl.MovieJsonConverter;
import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Movie;
import repository.entity_repository.impl.MovieRepository;
import validators.impl.MovieValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static utils.others.SimulateTimeFlowUtils.getClock;
import static utils.others.UserDataUtils.*;


@RequiredArgsConstructor
public class MovieService {

  private final MovieRepository movieRepository;

  private Movie createMovie(final String jsonfileName) {

    return new MovieJsonConverter(jsonfileName)
            .fromJson()
            .orElseThrow(() -> new AppException("FILE " + jsonfileName + " is empty"));
  }

  public boolean addMovie(final String jsonFileName) {
    Movie movie = createMovie(jsonFileName);
    boolean isValid = new MovieValidator().validateEntity(movie);
    if (isValid) {
      movieRepository.add(movie);
    }
    return isValid;
  }

  public void deleteMovie(final Integer id) {
    movieRepository.delete(id);
  }

  public List<Movie> getAllMovies() {
    return movieRepository.findAll();
  }

  public Optional<Movie> findMovieById(final Integer id) {
    return movieRepository.findById(id);
  }

  public boolean updateMovie(Movie movie) {
    boolean isCorrect = new MovieValidator().validateEntity(movie);
    if (isCorrect) {
      movieRepository.update(movie);
    }
    return isCorrect;
  }

  public Movie chooseMovieById() {

    Optional<Movie> movieOptional;
    Integer movieId;
    do {
      printMessage("AVAILABLE MOVIES");
      printCollectionWithNumeration(getAllMovies());
      movieId = getInt("Input proper movie id");
      movieOptional = movieRepository.findById(movieId);
    } while (movieOptional.isEmpty());

    return movieOptional.get();
  }

  public LocalDateTime chooseMovieStartTime(Movie movie) {

    printMessage("Possible movie show times in the next 24 hours");
    printCollectionWithNumeration(possibleShowTimes(movie));
    LocalDateTime movieStartTime;

    movieStartTime = getLocalDateTime("Input movie start time in format 'year-month-day HH:mm'");

    return movieStartTime;
  }

  private List<LocalDateTime> possibleShowTimes(Movie movie) {

    var presentDateTime = LocalDateTime.now(getClock());

    var localDateTimeToClosesHourOrHalfAnHour = presentDateTime.getMinute() < 30 ? presentDateTime.truncatedTo(ChronoUnit.HOURS).plusMinutes(30) : presentDateTime.plusHours(1).truncatedTo(ChronoUnit.HOURS);
    var seedDateTime = movie.getReleaseDate().compareTo(presentDateTime.toLocalDate()) < 0 ? localDateTimeToClosesHourOrHalfAnHour : movie.getReleaseDate().atTime(8, 0);

    return Stream.iterate(seedDateTime, date -> date.plusMinutes(30))
            .limit(ChronoUnit.HOURS.between(seedDateTime, seedDateTime.plusHours(48)))
            .filter(date -> date.toLocalTime().compareTo(LocalTime.of(8, 0)) >= 0 && date.toLocalTime().compareTo(LocalTime.of(22, 30)) <= 0)
            .collect(Collectors.toList());
  }

  public Map<String, Double> getAverageMovieDurationForMovieCategory() {

    return movieRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(Movie::getGenre, Collectors.averagingInt(Movie::getDuration)));
  }

  public Map<String, List<Movie>> mostExpensiveMoviesForEachGenre() {

    return movieRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(Movie::getGenre, Collectors.maxBy(Comparator.comparing(Movie::getPrice, BigDecimal::compareTo))))
            .entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> movieRepository.findAll().stream().filter(movie -> movie.getGenre().equals(e.getKey()) && movie.getPrice().compareTo(e.getValue().get().getPrice()) == 0).collect(Collectors.toList())));
  }

  public Map<String, List<Movie>> cheapestMoviesForEachGenre() {

    return movieRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(Movie::getGenre, Collectors.minBy(Comparator.comparing(Movie::getPrice, BigDecimal::compareTo))))
            .entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> movieRepository.findAll().stream().filter(movie -> movie.getGenre().equals(e.getKey()) && movie.getPrice().compareTo(e.getValue().get().getPrice()) == 0).collect(Collectors.toList())));
  }

  public Map<String, Map<LocalDate, List<Movie>>> theEarliestPremierInMovieGenre() {

    return movieRepository.findAll()
            .stream().collect(Collectors.groupingBy(Movie::getGenre, Collectors.minBy(Comparator.comparing(Movie::getReleaseDate, LocalDate::compareTo))))
            .entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> movieRepository.findAll().stream().filter(movie -> movie.getGenre().equals(e.getKey()) && movie.getReleaseDate().compareTo(e.getValue().get().getReleaseDate()) == 0).collect(Collectors.groupingBy(Movie::getReleaseDate))));
  }
}
