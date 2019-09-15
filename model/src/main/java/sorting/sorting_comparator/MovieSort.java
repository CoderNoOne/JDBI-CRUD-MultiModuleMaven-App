package sorting.sorting_comparator;

import exceptions.AppException;
import entity.Movie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MovieSort {

  private List<Comparator<Movie>> comparators;

  private MovieSort(MovieSortBuilder movieSortBuilder) {
    comparators = movieSortBuilder.comparators;
  }

  public Comparator<Movie> getFinalComparator() {

    if (comparators.isEmpty()) {
      throw new AppException("NO COMPARATORS AVAILABLE");
    }
    Comparator<Movie> baseComparator = comparators.get(0);

    return getComparatorRecursive(baseComparator, comparators, 0);
  }


  private Comparator<Movie> getComparatorRecursive(Comparator<Movie> baseComparator, List<Comparator<Movie>> comparators, Integer counter) {

    if (counter == comparators.size() - 1) {
      return baseComparator;
    }

    baseComparator = baseComparator.thenComparing(comparators.get(++counter));

    return getComparatorRecursive(baseComparator, comparators, counter);

  }


  public static class MovieSortBuilder {

    private List<Comparator<Movie>> comparators = new ArrayList<>();

    public MovieSortBuilder title(boolean isAscendingOrder) {
      comparators.add(isAscendingOrder ? Comparator.comparing(Movie::getTitle) : Comparator.comparing(Movie::getTitle).reversed());
      return this;
    }

    public MovieSortBuilder genre(boolean isAscendingOrder) {
      comparators.add(isAscendingOrder ? Comparator.comparing(Movie::getGenre) : Comparator.comparing(Movie::getGenre).reversed());
      return this;
    }

    public MovieSortBuilder price(boolean isAscendingOrder) {
      comparators.add(isAscendingOrder ? Comparator.comparing(Movie::getPrice) : Comparator.comparing(Movie::getPrice).reversed());
      return this;
    }

    public MovieSortBuilder duration(boolean isAscendingOrder) {
      comparators.add(isAscendingOrder ? Comparator.comparing(Movie::getDuration) : Comparator.comparing(Movie::getDuration).reversed());
      return this;
    }

    public MovieSortBuilder releaseDate(boolean isAscendingOrder) {
      comparators.add(isAscendingOrder ? Comparator.comparing(Movie::getReleaseDate) : Comparator.comparing(Movie::getReleaseDate).reversed());
      return this;
    }

    public MovieSort build() {
      return new MovieSort(this);
    }
  }
}
