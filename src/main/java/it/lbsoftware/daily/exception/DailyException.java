package it.lbsoftware.daily.exception;

public class DailyException extends RuntimeException {
  public DailyException(final String errorCode) {
    super(errorCode);
  }
}
