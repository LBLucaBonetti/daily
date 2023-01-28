package it.lbsoftware.daily.exception;

public class DailyBadRequestException extends RuntimeException {
  public DailyBadRequestException(final String errorCode) {
    super(errorCode);
  }
}
