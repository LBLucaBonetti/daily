package it.lbsoftware.daily.config;

import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorConfiguration implements ErrorController {

  @RequestMapping("/error")
  public String handleError(HttpServletResponse httpServletResponse) {
    // Change response status code to avoid having the error popping up in the browser console.
    // The error is still picked up by the frontend router and handled showing the 404 page.
    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    return "forward:/";
  }
}
