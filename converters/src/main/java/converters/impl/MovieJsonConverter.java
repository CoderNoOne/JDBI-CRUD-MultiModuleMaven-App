package converters.impl;

import converters.JsonConverter;
import model.entity.Movie;

public class MovieJsonConverter extends JsonConverter<Movie> {
  public MovieJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
