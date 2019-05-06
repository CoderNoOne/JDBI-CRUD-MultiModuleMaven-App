package validators;

import java.util.Map;
import java.util.stream.Collectors;

public interface Validator<T> {
  Map<String, String> validate(T t);

  boolean hasErrors();

  boolean validateEntity(T t);


}
