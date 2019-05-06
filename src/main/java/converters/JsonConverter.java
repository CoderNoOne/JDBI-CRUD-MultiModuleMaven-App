package converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.AppException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public abstract class JsonConverter<T> {

  private final String jsonFilename;

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private final Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

  public JsonConverter(String jsonFilename) {
    this.jsonFilename = jsonFilename;
  }

  public void toJson(final T element) {
    try (FileWriter fileWriter = new FileWriter(jsonFilename)) {
      if (element == null) {
        throw new NullPointerException("ELEMENT IS NULL");
      }
      gson.toJson(element, fileWriter);
    } catch (Exception e) {
      // System.out.println(e.getMessage());
      // System.err.println(Arrays.toString(e.getStackTrace()));
      log.error(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
      throw new AppException("TO JSON CONVERSION EXCEPTION");
    }
  }

  public Optional<T> fromJson() {
    try (FileReader fileReader = new FileReader(jsonFilename)) {
      return Optional.of(gson.fromJson(fileReader, type));
    } catch (Exception e) {
      System.err.println(Arrays.toString(e.getStackTrace()));
      System.out.println(e.getMessage());
      throw new AppException("FROM JSON CONVERSION EXCEPTION");
    }
  }
}

