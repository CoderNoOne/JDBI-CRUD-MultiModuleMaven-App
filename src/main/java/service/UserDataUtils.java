package service;

import exceptions.AppException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class UserDataUtils {

  private UserDataUtils() {}

  private static Scanner sc = new Scanner(System.in);

  public static int getInt(String message) {
    System.out.println(message);

    String text = sc.nextLine();
    if (!text.matches("[\\d]+")) {
      throw new AppException(("INT VALUE IS NOT CORRECT: " + text));
    }

    return Integer.parseInt(text);
  }

  public static String getString(String inputMessage) {

    System.out.println(inputMessage);

    String input = sc.nextLine();

    if (input.length() == 0) {
      throw new AppException("YOU DIDN'T INPUT ANY VALUE");
    }

    return input;
  }

  public static void close() {
    if (sc != null) {
      sc.close();
      sc = null;
    }
  }

  public String getDate(String message) {
    System.out.println(message);

    String date = sc.nextLine();

    try {
      LocalDate.parse(date);
    } catch (DateTimeParseException e) {
      System.err.println(Arrays.toString(e.getStackTrace()));
      throw new AppException("DATE FORMAT NOT SUPPORTED");
    }
    return date;
  }

  public BigDecimal getBigDecimal(String inputMessage) {
    System.out.println(inputMessage);

    return sc.nextBigDecimal();

  }
}
