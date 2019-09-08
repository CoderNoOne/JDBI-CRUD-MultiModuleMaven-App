package converters.impl;

import converters.JsonConverter;
import model.entity.Movie;

import java.util.List;

public class MovieListJsonConverter extends JsonConverter<List<Movie>> {
  public MovieListJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
