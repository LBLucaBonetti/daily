package it.lbsoftware.daily.exception;

public class DailyConflictException extends RuntimeException {
  public DailyConflictException(final String errorCode) {
    super(errorCode);
  }
}
