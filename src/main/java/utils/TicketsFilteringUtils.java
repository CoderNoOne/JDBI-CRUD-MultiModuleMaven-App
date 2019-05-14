package utils;

import exceptions.AppException;
import model.tickets_data_filtering.MovieFilterCommand;
import model.tickets_data_filtering.MovieFilteringCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static model.tickets_data_filtering.MovieFilteringCriterion.*;
import static utils.UserDataUtils.*;

public class TicketsFilteringUtils {

  private static MovieFilterCommand.FilterCommandBuilder builder;
  private static List<MovieFilteringCriterion> filteringCriteria;
  private static Logger logger = LoggerFactory.getLogger(TicketsFilteringUtils.class);

  private TicketsFilteringUtils() {
  }

  public static MovieFilterCommand getMovieFilters(String message) {
    filteringCriteria = new ArrayList<>(Arrays.asList(values()));
    builder = new MovieFilterCommand.FilterCommandBuilder();
    System.out.println(message);

    while (true) {
      MovieFilteringCriterion filteringCriterion;
      filteringCriterion = valueOf(getString("CHOOSE FILTERING CRITERION FROM BELOW: \n " + filteringCriteria).toUpperCase());
      if (!filteringCriteria.contains(filteringCriterion)) {
        throw new AppException("Filtered criterion has been already selected!");
      }
      switch (filteringCriterion) {
        case MOVIE_DURATION -> {
          filterByMovieDuration();
          filteringCriteria.remove(MOVIE_DURATION);
        }
        case MOVIE_RELEASE_DATE -> {
          filterByReleaseDate();
          filteringCriteria.remove(MOVIE_RELEASE_DATE);
        }
        case MOVIE_GENRE -> {
          filterByGenre();
          filteringCriteria.remove(MOVIE_GENRE);
        }
      }
      if (filteringCriteria.isEmpty() || !getString("DO YOU WANT TO ADD NEW SORTING CRITERION? Y/N").equalsIgnoreCase("Y"))
        break;
    }
    return builder.build();

  }

  private static void filterByGenre() {

    List<String> movieGenres = new ArrayList<>();
    boolean isNext;
    do {
      var genre = getString("Input movie genre");
      movieGenres.add(genre);
      isNext = getString("Do you want to add next movie genre? (Y/N)").equalsIgnoreCase("Y");
    } while (isNext);

    builder.genre(movieGenres);
  }

  private static void filterByReleaseDate() {
    var minimumReleaseDate = getLocalDate("Input minimum release date");
    var maximumReleaseDate = getLocalDate("Input maximum release date");
    builder.releaseDate(minimumReleaseDate, maximumReleaseDate);

  }

  private static void filterByMovieDuration() {
    var minDuration = getInt("Choose the min movie duration");
    var maxDuration = getInt("Choose the max movie duration");
    builder.duration(minDuration, maxDuration);

  }

}
