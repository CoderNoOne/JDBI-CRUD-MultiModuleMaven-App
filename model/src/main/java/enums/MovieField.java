package enums;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public enum MovieField {
  TITLE, GENRE, PRICE, DURATION, RELEASE_DATE;

  public static MovieField fromString(String value) {

    MovieField movieField = null;

    try {
      movieField = valueOf(value);
    } catch (Exception e) {
      log.info(e.getMessage());
      log.error(Arrays.toString(e.getStackTrace()));
    }

    return movieField;
  }
}
