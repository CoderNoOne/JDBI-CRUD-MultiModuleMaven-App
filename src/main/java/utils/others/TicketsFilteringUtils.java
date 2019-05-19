package utils.others;

import exceptions.AppException;
import lombok.extern.slf4j.Slf4j;
import model.tickets_data_filtering.MovieFilterCommand;
import model.tickets_data_filtering.MovieFilteringCriterion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static model.tickets_data_filtering.MovieFilteringCriterion.*;
import static utils.others.UserDataUtils.*;

@Slf4j
public class TicketsFilteringUtils {

  private static MovieFilterCommand.FilterCommandBuilder builder;
  private static List<MovieFilteringCriterion> filteringCriteria;

  private TicketsFilteringUtils() {
  }

  private static void printCriteria(List<MovieFilteringCriterion> movieFilters) {
    AtomicInteger counter = new AtomicInteger(1);
    filteringCriteria.forEach(criterion -> {
      System.out.println("Criterion no. " + counter.getAndIncrement() + " - > " + criterion);
    });
  }

  public static MovieFilterCommand inputMovieFilters(String message) {
    filteringCriteria = new ArrayList<>(Arrays.asList(values()));
    builder = new MovieFilterCommand.FilterCommandBuilder();
    System.out.println(message);

    while (true) {

      MovieFilteringCriterion filteringCriterion;
      do {
        printCriteria(filteringCriteria);
        filteringCriterion = valueOf(getString("CHOOSE PROPER FILTERING CRITERION FROM ABOVE: (NOT CASE SENSITIVE) \n ").toUpperCase());
      } while (!filteringCriteria.contains(filteringCriterion));

      switch (filteringCriterion) {
        case DURATION -> {
          filterByMovieDuration();
          filteringCriteria.remove(DURATION);
        }
        case RELEASE_DATE -> {
          filterByReleaseDate();
          filteringCriteria.remove(RELEASE_DATE);
        }
        case GENRE -> {
          filterByGenre();
          filteringCriteria.remove(GENRE);
        }
      }
      if (filteringCriteria.isEmpty() || !getString("DO YOU WANT TO ADD NEW SORTING CRITERION? Y/N").equalsIgnoreCase("Y"))
        break;
    }
    return builder.build();
  }

  private static void filterByGenre() {

    List<String> movieGenres = new ArrayList<>();
    boolean hasNext;
    do {
      var genre = getString("Input movie genre");
      movieGenres.add(genre);
      hasNext = getString("Do you want to add next movie genre? (Y/N)").equalsIgnoreCase("Y");
    } while (hasNext);

    builder.genre(movieGenres);
  }

  private static void filterByReleaseDate() {

    LocalDate minimumReleaseDate = LocalDate.now(), maximumReleaseDate = LocalDate.now();
    boolean isValid = false;

    do {
      try {
        minimumReleaseDate = getLocalDate("Input minimum release date");
        maximumReleaseDate = getLocalDate("Input maximum release date");
        if (!(isValid = minimumReleaseDate.compareTo(maximumReleaseDate) <= 0)) {
          System.out.println("Min release date has to be greater or equal to max release date!");
        }
      } catch (AppException e) {
        log.info(e.getMessage(), e);
      }
    } while (!isValid);

    builder.releaseDate(minimumReleaseDate, maximumReleaseDate);

  }

  private static void filterByMovieDuration() {

    int minDuration = 0, maxDuration = 0;
    boolean isValid = false;

    do {
      try {
        minDuration = getInt("Choose the min movie duration");
        maxDuration = getInt("Choose the max movie duration");
        if (!(isValid = minDuration <= maxDuration)) {
          System.out.println("Min duration has to be greater or equal to max duration!");
        }
      } catch (AppException e) {
        log.info(e.getMessage(), e);
      }
    } while (!isValid);

    builder.duration(minDuration, maxDuration);
  }

}
