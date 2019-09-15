package converters.impl;

import converters.JsonConverter;
import entity.Movie;
import lombok.Data;


public class MovieJsonConverter extends JsonConverter<Movie> {
  public MovieJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
