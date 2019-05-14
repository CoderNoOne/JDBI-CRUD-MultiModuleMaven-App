package utils;

import exceptions.AppException;
import model.sorting.MovieSort;
import model.sorting.MovieSortingCriterion;

import java.util.Arrays;
import java.util.List;

import static model.sorting.CustomerSortingCriterion.*;
import static model.sorting.CustomerSortingCriterion.NAME;
import static model.sorting.MovieSortingCriterion.TITLE;
import static utils.UserDataUtils.getString;

public class MovieSortingUtils {

  private static MovieSort.MovieSortBuilder builder;
  private static List<MovieSortingCriterion> sortingAlgorithms;

  private MovieSortingUtils() {
  }

  public static MovieSort getMovieSortingAlgorithm(String message) {

    sortingAlgorithms = Arrays.asList(MovieSortingCriterion.values());
    builder = new MovieSort.MovieSortBuilder();
    System.out.println(message);

    while (true) {

      MovieSortingCriterion sortingCriterion = MovieSortingCriterion.valueOf(getString("CHOOSE FROM ABOVE: " + sortingAlgorithms).toUpperCase());

      if (!sortingAlgorithms.contains(sortingCriterion)) throw new AppException("UNDEFINED SORTING CRITERION OR ALREADY SORTED BY THIS ONE");

      switch (sortingCriterion) {
        case TITLE -> sortByTitle();
        case GENRE -> sortByGenre();
        case DURATION -> sortByDuration();
        case RELEASE_DATE -> sortByReleaseDate();
      }
      if (!getString("DO YOU WANT TO ADD NEW SORTING CRITERION? Y/N").equalsIgnoreCase("Y")) break;
    }
    return builder.build();
  }

  private static void sortByTitle() {
    builder = chooseOrdering().equals("ASC") ? builder.title(true) : builder.title(false);
    sortingAlgorithms.remove(TITLE);
  }

  private static void sortByGenre() {
    builder = chooseOrdering().equals("ASC") ? builder.genre(true) : builder.genre(false);
    sortingAlgorithms.remove(AGE);
  }

  private static void sortByDuration() {
    builder = chooseOrdering().equals("ASC") ? builder.duration(true) : builder.duration(false);
    sortingAlgorithms.remove(SURNAME);
  }

  private static void sortByReleaseDate() {
    builder = chooseOrdering().equals("ASC") ? builder.releaseDate(true) : builder.releaseDate(false);
    sortingAlgorithms.remove(NAME);
  }

  private static String chooseOrdering() {
    return getString("CHOOSE ORDERING. ASC/DESC").toUpperCase();
  }
}

}
