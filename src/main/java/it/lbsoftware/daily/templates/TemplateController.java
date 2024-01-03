package it.lbsoftware.daily.templates;

import static it.lbsoftware.daily.templates.TemplateUtils.redirectIfAuthenticated;

import it.lbsoftware.daily.config.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@CommonsLog
class TemplateController {

  @GetMapping(path = Constants.LOGIN_PATH)
  public String login(Authentication authentication) {
    return redirectIfAuthenticated(authentication).orElse(Constants.LOGIN_VIEW);
  }
}
