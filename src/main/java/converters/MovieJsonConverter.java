package converters;

import model.Movie;

public class MovieJsonConverter extends JsonConverter<Movie> {
  public MovieJsonConverter(String jsonFilename) {
    super(jsonFilename);
  }
}
