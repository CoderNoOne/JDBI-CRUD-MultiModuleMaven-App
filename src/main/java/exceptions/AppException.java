package exceptions;

public class AppException extends RuntimeException {
  private final String exceptionMessage;

  public AppException(String exceptionMessage) {
    this.exceptionMessage = exceptionMessage;
  }

  public String getExceptionMessage() {
    return exceptionMessage;
  }
}

