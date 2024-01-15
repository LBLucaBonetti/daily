package it.lbsoftware.daily.appuserlogins;

import static it.lbsoftware.daily.templates.TemplateUtils.redirectIfAuthenticated;

import it.lbsoftware.daily.config.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = Constants.LOGIN_PATH)
@RequiredArgsConstructor
@CommonsLog
class AppUserLoginController {

  @GetMapping
  public String login(Authentication authentication) {
    return redirectIfAuthenticated(authentication).orElse(Constants.LOGIN_VIEW);
  }
}
