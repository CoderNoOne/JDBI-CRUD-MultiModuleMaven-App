package utils.others;

import exceptions.AppException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class UserDataUtils {

  private UserDataUtils() {
  }

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

    printMessage(inputMessage);

    String input = sc.nextLine();

    if (input.length() == 0) {
      throw new AppException("YOU DIDN'T INPUT ANY VALUE");
    }

    return input;
  }

  public static void printCollectionWithNumeration(Collection<? extends Object> collectionToPrint) {
    AtomicInteger counter = new AtomicInteger(1);
    collectionToPrint.forEach(obj -> printMessage("No. " + counter.getAndIncrement() + " : " + obj));
  }

  public static void printMessage(String message) {
    System.out.println(message);
  }

  public static void close() {
    if (sc != null) {
      sc.close();
      sc = null;
    }
  }

  public static LocalDate getDate(String message) {
    System.out.println(message);

    String date = sc.nextLine();
    LocalDate localDate;
    try {
      localDate = LocalDate.parse(date);
    } catch (DateTimeParseException e) {
      System.err.println(Arrays.toString(e.getStackTrace()));
      throw new AppException("DATE FORMAT NOT SUPPORTED");
    }
    return localDate;
  }

  public static BigDecimal getBigDecimal(String inputMessage) {
    System.out.println(inputMessage);

    return sc.nextBigDecimal();

  }

  public static LocalDateTime getLocalDateTime(String message) {
    System.out.println(message);

    String date = sc.nextLine();

    LocalDateTime localDateTime;
    try {
      localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    } catch (DateTimeParseException e) {
      System.err.println(Arrays.toString(e.getStackTrace()));
      throw new AppException("DATE FORMAT NOT SUPPORTED");
    }
    return localDateTime;
  }

  public static LocalDate getLocalDate(String message) {
    System.out.println(message);

    String date = sc.nextLine();

    LocalDate localDate;
    try {
      localDate = LocalDate.parse(date);
    } catch (DateTimeParseException e) {
      throw new AppException("DATE FORMAT NOT SUPPORTED");
    }
    return localDate;
  }


}
