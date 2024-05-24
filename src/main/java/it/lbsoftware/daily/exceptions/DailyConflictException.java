package it.lbsoftware.daily.exceptions;

/**
 * Represents a 409 HTTP status code with a message key that will be internationalized by the
 * frontend.
 */
public class DailyConflictException extends RuntimeException {
  public DailyConflictException(final String errorCode) {
    super(errorCode);
  }
}
