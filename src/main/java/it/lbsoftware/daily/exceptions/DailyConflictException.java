package it.lbsoftware.daily.exceptions;

public class DailyConflictException extends RuntimeException {
  public DailyConflictException(final String errorCode) {
    super(errorCode);
  }
}
