package service.others;

import lombok.RequiredArgsConstructor;
import model.entity.Movie;
import model.others.CustomerWithMoviesAndSalesStand;
import model.others.MovieWithSalesStand;
import repository.others.JoinedEntitiesRepository;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JoinedEntitiesService {

  private final JoinedEntitiesRepository joinedEntitiesRepository;

  public Map<Movie, Integer> movieGroupedByPopularity() {

    return joinedEntitiesRepository.getMovieWithSalesStand()
            .stream()
            .collect(Collectors.groupingBy(JoinedEntitiesService::convertMovieWithSalesStandToMovie, Collectors.summingInt(e -> 1)));
  }


  public Map<Integer, Map<String, Integer>> mostPopularMovieGenreForEachCustomer() {

    return joinedEntitiesRepository.getCustomerWithMoviesAndSalesStand()
            .stream()
            .collect(Collectors.groupingBy(CustomerWithMoviesAndSalesStand::getCustomerId,
                    Collectors.mapping(CustomerWithMoviesAndSalesStand::getMovieGenre,
                            Collectors.groupingBy(Function.identity(),
                                    Collectors.summingInt(e -> 1)))))
            .entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().entrySet().stream()
                            .filter(innerMap -> innerMap.getValue() >= e.getValue().entrySet().stream().max(Map.Entry.comparingByValue()).get().getValue())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
  }


  private static Movie convertMovieWithSalesStandToMovie(MovieWithSalesStand movieWithSalesStand) {
    return Movie.builder()
            .id(movieWithSalesStand.getMovieId())
            .title(movieWithSalesStand.getMovieTitle())
            .genre(movieWithSalesStand.getMovieGenre())
            .price(movieWithSalesStand.getMoviePrice())
            .duration(movieWithSalesStand.getMovieDuration())
            .releaseDate(movieWithSalesStand.getMovieReleaseDate())
            .build();
  }
}
