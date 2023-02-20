package it.lbsoftware.daily.exception;

import it.lbsoftware.daily.config.Constants;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DailyExceptionHandler {

  @ExceptionHandler(value = DailyNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handle(DailyNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(getExceptionBody(exception.getMessage()));
  }

  @ExceptionHandler(value = DailyConflictException.class)
  public ResponseEntity<Map<String, Object>> handle(DailyConflictException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(getExceptionBody(exception.getMessage()));
  }

  @ExceptionHandler(value = DailyBadRequestException.class)
  public ResponseEntity<Map<String, Object>> handle(DailyBadRequestException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(getExceptionBody(exception.getMessage()));
  }

  public Map<String, Object> getExceptionBody(final String errorCode) {
    return Map.of(
        Constants.ERROR_KEY,
        Optional.ofNullable(errorCode)
            .filter(ec -> ec.startsWith(Constants.ERROR_PREFIX))
            .orElse(Constants.ERROR_DEFAULT));
  }
}
