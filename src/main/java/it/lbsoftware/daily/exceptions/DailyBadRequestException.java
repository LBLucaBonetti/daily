package it.lbsoftware.daily.exceptions;

public class DailyBadRequestException extends RuntimeException {
  public DailyBadRequestException(final String errorCode) {
    super(errorCode);
  }
}
