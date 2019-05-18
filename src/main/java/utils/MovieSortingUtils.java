package utils;

import exceptions.AppException;
import model.sorting.sorting_comparator.MovieSort;
import model.sorting.sorting_criterion.MovieSortingCriterion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static model.sorting.sorting_criterion.MovieSortingCriterion.*;
import static utils.UserDataUtils.getString;
import static utils.UserDataUtils.printMessage;

public class MovieSortingUtils {

  private static MovieSort.MovieSortBuilder builder;
  private static List<MovieSortingCriterion> sortingAlgorithms;

  private MovieSortingUtils() {
  }

  public static MovieSort getMovieSortingAlgorithm(String message) {

    printMessage(message);
    sortingAlgorithms = new ArrayList<>(Arrays.asList(MovieSortingCriterion.values()));
    builder = new MovieSort.MovieSortBuilder();

    while (true) {

      var sortingCriterion = MovieSortingCriterion.valueOf(getString("CHOOSE FROM ABOVE: " + sortingAlgorithms).toUpperCase());

      if (!sortingAlgorithms.contains(sortingCriterion))
        throw new AppException("UNDEFINED SORTING CRITERION OR ALREADY SORTED BY THIS ONE");

      switch (sortingCriterion) {
        case TITLE -> sortByTitle();
        case GENRE -> sortByGenre();
        case DURATION -> sortByDuration();
        case RELEASE_DATE -> sortByReleaseDate();
        case PRICE -> sortByPrice();
      }
      if (sortingAlgorithms.isEmpty() || !getString("DO YOU WANT TO ADD NEW SORTING CRITERION? Y/N").equalsIgnoreCase("Y"))
        break;
    }
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


