package validators;

import java.util.Map;

public interface Validator<T> {
  Map<String, String> validate(T t, boolean isUpdate);
  boolean hasErrors();
  boolean validateEntity(T t, boolean isUpdate);
}
