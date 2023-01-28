package it.lbsoftware.daily.exception;

public class DailyNotFoundException extends RuntimeException {
  public DailyNotFoundException(final String errorCode) {
    super(errorCode);
  }
}
