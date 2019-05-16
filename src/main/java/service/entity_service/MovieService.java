package service.entity_service;

import converters.impl.MovieJsonConverter;
import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Movie;
import repository.impl.MovieRepository;
import utils.UserDataUtils;
import validators.impl.MovieValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

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

  public Movie chooseMovieById() {

    System.out.println("AVAILABLE MOVIES");
    showAllMovies();

    Integer movieId = UserDataUtils.getInt("Input movie id");

    return movieRepository.findById(movieId).orElseThrow(() -> new AppException(""));
  }

  public LocalDateTime chooseMovieStartTime( Movie movie) {

//    System.out.println("Choose movie start time in format 'year-month-day HH:mm'");
    possibleShowTimes(movie);

    //weryfikacja czy wybrał dobrze
    return UserDataUtils.getLocalDateTime("Input movie start time in format 'year-month-day HH:mm'");
  }

  private void possibleShowTimes(Movie movie) {

    var presentDateTime = LocalDateTime.now();

    var localDateTimeToClosesHourOrHalfAnHour = presentDateTime.getMinute() < 30 ? presentDateTime.truncatedTo(ChronoUnit.HOURS).plusMinutes(30) : presentDateTime.plusHours(1).truncatedTo(ChronoUnit.HOURS);
    var seedDateTime = movie.getReleaseDate().compareTo(presentDateTime.toLocalDate()) < 0 ? localDateTimeToClosesHourOrHalfAnHour : movie.getReleaseDate().atTime(8,0);

    //wykaz godzin potenjalnych seansow na nastpne 24 h od teraz
    Stream.iterate(seedDateTime, date -> date.plusMinutes(30))
            .limit(ChronoUnit.HOURS.between(seedDateTime, presentDateTime.plusHours(50)))
            .filter(date -> date.toLocalTime().compareTo(LocalTime.of(8, 0)) >= 0 && date.toLocalTime().compareTo(LocalTime.of(22, 30)) <= 0)
            .forEach(System.out::println);
  }

  public void chooseMovieToWatch() {
    var movie = chooseMovieById();
    chooseMovieStartTime(movie);
  }
}
