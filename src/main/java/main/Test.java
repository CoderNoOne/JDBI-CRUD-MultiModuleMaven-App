package main;

import model.Movie;
import repository.MovieRepository;

import java.math.BigDecimal;

public class Test {

  public static void main(String[] args) {

    var movieRepository = new MovieRepository();

    movieRepository.add(Movie.builder().title("Avengers")
            .duration(2)
            .genre("sci-fi")
            .price(new BigDecimal("50.25"))
            .releaseDate("2020-10-20")
            .build());

  }

}
