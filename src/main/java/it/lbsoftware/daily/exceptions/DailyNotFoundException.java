package it.lbsoftware.daily.exceptions;

public class DailyNotFoundException extends RuntimeException {
  public DailyNotFoundException(final String errorCode) {
    super(errorCode);
  }
}
