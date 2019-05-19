package service.entity_service;

import converters.impl.MovieJsonConverter;
import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import model.entity.Movie;
import repository.entity_repository.impl.MovieRepository;
import utils.others.UserDataUtils;
import validators.impl.CustomerValidator;
import validators.impl.MovieValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static utils.others.UserDataUtils.*;
import static utils.others.UserDataUtils.printCollectionWithNumeration;
import static utils.others.UserDataUtils.printMessage;


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

  public boolean updateMovieDetail(Integer id, String title, String genre, LocalDate releaseDate, Integer duration, BigDecimal price) {

    Movie movie = Movie.builder().id(id).title(title).genre(genre).price(price).duration(duration).releaseDate(releaseDate).build();

    boolean isValid = new MovieValidator().validateEntity(movie);

    if (isValid) {
      updateMovie(movie);
    }
    return isValid;
  }

  private Movie chooseMovieById() {

    printMessage("AVAILABLE MOVIES");
    printCollectionWithNumeration(getAllMovies());

    Integer movieId = getInt("Input movie id");

    return movieRepository.findById(movieId).orElseGet(this::getMovieAgain);
  }

  public Map<String, Object> chooseMovieStartTime() {

    var movie = chooseMovieById();
    printMessage("Possible movie show times in the next 24 hours");
    printCollectionWithNumeration(possibleShowTimes(movie));
    LocalDateTime movieStartTime;

    movieStartTime = getLocalDateTime("Input movie start time in format 'year-month-day HH:mm'");

    return Map.of("movie", movie, "movieStartTime", movieStartTime);
  }

  private List<LocalDateTime> possibleShowTimes(Movie movie) {

    var presentDateTime = LocalDateTime.now();

    var localDateTimeToClosesHourOrHalfAnHour = presentDateTime.getMinute() < 30 ? presentDateTime.truncatedTo(ChronoUnit.HOURS).plusMinutes(30) : presentDateTime.plusHours(1).truncatedTo(ChronoUnit.HOURS);
    var seedDateTime = movie.getReleaseDate().compareTo(presentDateTime.toLocalDate()) < 0 ? localDateTimeToClosesHourOrHalfAnHour : movie.getReleaseDate().atTime(8, 0);

    //wykaz godzin potenjalnych seansow na nastpne 24 h od teraz
    return Stream.iterate(seedDateTime, date -> date.plusMinutes(30))
            .limit(ChronoUnit.HOURS.between(seedDateTime, seedDateTime.plusHours(48)))
            .filter(date -> date.toLocalTime().compareTo(LocalTime.of(8, 0)) >= 0 && date.toLocalTime().compareTo(LocalTime.of(22, 30)) <= 0)
            .collect(Collectors.toList());
  }


  private Movie getMovieAgain() {
    printMessage("That film isn't in our database. Check again");
    return chooseMovieById();
  }
}
