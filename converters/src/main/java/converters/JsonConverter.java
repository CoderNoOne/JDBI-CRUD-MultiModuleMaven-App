package converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.AppException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class JsonConverter<T> {

  private final String jsonFilename;
  private final Gson gson;
  private final Type type;

  public JsonConverter(String jsonFilename) {
    gson = new GsonBuilder().setPrettyPrinting().create();
    type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    this.jsonFilename = jsonFilename;
  }

  public Optional<T> fromJson() {
    try (FileReader fileReader = new FileReader(jsonFilename)) {
      return Optional.ofNullable(gson.fromJson(fileReader, type));
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException("FROM JSON CONVERSION EXCEPTION");
    }
  }
}

