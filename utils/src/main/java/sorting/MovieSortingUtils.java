package sorting;

import enums.MovieField;
import sorting.sorting_comparator.MovieSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static enums.MovieField.*;
import static others.UserDataUtils.getString;
import static others.UserDataUtils.printMessage;

public final class MovieSortingUtils {

  private static MovieSort.MovieSortBuilder builder;
  private static List<MovieField> sortingAlgorithms;

  private MovieSortingUtils() {
  }

  public static MovieSort getMovieSortingAlgorithm(String message) {

    printMessage(message);
    sortingAlgorithms = new ArrayList<>(Arrays.asList(MovieField.values()));
    builder = new MovieSort.MovieSortBuilder();

    boolean hasNext;
    do {
      MovieField sortingCriterion;

      do {
        sortingCriterion = MovieField.fromString(getString("CHOOSE FROM ABOVE: " + sortingAlgorithms).toUpperCase());
      } while (sortingCriterion == null);

      switch (sortingCriterion) {
        case TITLE -> sortByTitle();
        case GENRE -> sortByGenre();
        case DURATION -> sortByDuration();
        case RELEASE_DATE -> sortByReleaseDate();
        case PRICE -> sortByPrice();
      }
      hasNext = getString("DO YOU WANT TO ADD NEW SORTING CRITERION? Y/N").equalsIgnoreCase("Y");

    } while (hasNext && !sortingAlgorithms.isEmpty());
    return builder.build();
  }

  private static void sortByPrice() {
    builder = chooseOrdering().equals("ASC") ? builder.title(true) : builder.title(false);
    sortingAlgorithms.remove(PRICE);
  }

  private static void sortByTitle() {
    builder = chooseOrdering().equals("ASC") ? builder.title(true) : builder.title(false);
    sortingAlgorithms.remove(TITLE);
  }

  private static void sortByGenre() {
    builder = chooseOrdering().equals("ASC") ? builder.genre(true) : builder.genre(false);
    sortingAlgorithms.remove(GENRE);
  }

  private static void sortByDuration() {
    builder = chooseOrdering().equals("ASC") ? builder.duration(true) : builder.duration(false);
    sortingAlgorithms.remove(DURATION);
  }

  private static void sortByReleaseDate() {
    builder = chooseOrdering().equals("ASC") ? builder.releaseDate(true) : builder.releaseDate(false);
    sortingAlgorithms.remove(RELEASE_DATE);
  }

  private static String chooseOrdering() {
    return getString("CHOOSE ORDERING. ASC/DESC").toUpperCase();
  }
}


