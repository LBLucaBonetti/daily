package it.lbsoftware.daily.exceptions;

/** Represents an error caused by a password that is not enough secure. */
public class DailyNotEnoughSecurePasswordException extends RuntimeException {

  public DailyNotEnoughSecurePasswordException(final String message) {
    super(message);
  }
}
