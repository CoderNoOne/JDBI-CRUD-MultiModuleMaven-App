package model.tickets_data_filtering;

import java.time.LocalDate;
import java.util.*;

import static model.tickets_data_filtering.MovieFilteringCriterion.*;

public class MovieFilterCommand {

  private Map<MovieFilteringCriterion, List<? extends Object>> filters;

  private MovieFilterCommand(FilterCommandBuilder filterCommandBuilder) {
    filters = filterCommandBuilder.filters;
  }

  public Map<MovieFilteringCriterion, List<? extends Object>> getFilters() {
    return filters;
  }

  public static class FilterCommandBuilder {

    private Map<MovieFilteringCriterion,  List<? extends Object>> filters = new EnumMap<>(MovieFilteringCriterion.class);

    public MovieFilterCommand.FilterCommandBuilder genre(List<String> genre) {
      filters.put(MOVIE_GENRE, genre);
      return this;
    }

    public MovieFilterCommand.FilterCommandBuilder duration(int minDuration, int maxDuration) {
      filters.put(MOVIE_DURATION, new ArrayList<>(Arrays.asList(minDuration, maxDuration)));
      return this;
    }

    public MovieFilterCommand.FilterCommandBuilder releaseDate(LocalDate minReleaseDate, LocalDate maxReleaseDate) {
      filters.put(MOVIE_RELEASE_DATE, new ArrayList<>(Arrays.asList(minReleaseDate, maxReleaseDate)));
      return this;
    }


    public MovieFilterCommand build() {
      return new MovieFilterCommand(this);
    }
  }
}
