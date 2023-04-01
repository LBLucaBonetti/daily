package it.lbsoftware.daily.config;

import it.lbsoftware.daily.exception.DailyExceptionHandler;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${server.error.path}")
@RequiredArgsConstructor
class DailyErrorController implements ErrorController {

  private final DailyExceptionHandler dailyExceptionHandler;

  @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
  public String handleError(
      HttpServletResponse httpServletResponse, Authentication authentication) {
    return Optional.ofNullable(authentication)
        .map(
            auth -> {
              // Change response status code to avoid having the error popping up in the browser
              // console.
              // The error is still picked up by the frontend router and handled showing the 404
              // page.
              httpServletResponse.setStatus(HttpServletResponse.SC_OK);
              return "forward:/";
            })
        .orElseGet(() -> Constants.ERROR_VIEW);
  }

  @RequestMapping
  public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
    HttpStatus status = getStatus(request);
    String error = getError(request);

    return new ResponseEntity<>(dailyExceptionHandler.getExceptionBody(error), status);
  }

  private HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    if (statusCode == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    try {
      return HttpStatus.valueOf(statusCode);
    } catch (Exception ex) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }

  private String getError(HttpServletRequest request) {
    return Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_MESSAGE))
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .filter(StringUtils::isNotBlank)
        .orElse(Constants.ERROR_DEFAULT);
  }
}
