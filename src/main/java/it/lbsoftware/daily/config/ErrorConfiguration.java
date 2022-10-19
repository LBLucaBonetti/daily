package it.lbsoftware.daily.config;

import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${server.error.path}")
public class ErrorConfiguration implements ErrorController {

  @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
  public String handleError(HttpServletResponse httpServletResponse) {
    // Change response status code to avoid having the error popping up in the browser console.
    // The error is still picked up by the frontend router and handled showing the 404 page.
    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    return "forward:/";
  }

  @RequestMapping
  public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
    HttpStatus status = getStatus(request);
    return new ResponseEntity<>(status);
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
}
