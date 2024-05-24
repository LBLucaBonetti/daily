package it.lbsoftware.daily.exceptions;

import it.lbsoftware.daily.config.Constants;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A container to define common exception mappings; each exception definition is bound to a specific
 * HTTP status code.
 */
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

  /**
   * Composes a body consisting of the error key and a value of a default or provided error code.
   *
   * @param errorCode The error code to provide; it should start with a predefined constant
   * @return A map with the error key and the error code as a value; the value is a default one if
   *     the provided one is not valid
   */
  public Map<String, Object> getExceptionBody(final String errorCode) {
    return Map.of(
        Constants.ERROR_KEY,
        Optional.ofNullable(errorCode)
            .filter(ec -> ec.startsWith(Constants.ERROR_PREFIX))
            .orElse(Constants.ERROR_DEFAULT));
  }
}
