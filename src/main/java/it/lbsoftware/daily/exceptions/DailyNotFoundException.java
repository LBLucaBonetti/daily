package it.lbsoftware.daily.exceptions;

/**
 * Represents a 404 HTTP status code with a message key that will be internationalized by the
 * frontend.
 */
public class DailyNotFoundException extends RuntimeException {
  public DailyNotFoundException(final String errorCode) {
    super(errorCode);
  }
}
