package it.lbsoftware.daily.exceptions;

/**
 * Represents a 400 HTTP status code with a message key that will be internationalized by the
 * frontend.
 */
public class DailyBadRequestException extends RuntimeException {
  public DailyBadRequestException(final String errorCode) {
    super(errorCode);
  }
}
