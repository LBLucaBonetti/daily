package it.lbsoftware.daily.appuserlogins;

import static it.lbsoftware.daily.templates.TemplateUtils.getInvalidCredentialsErrorMessage;
import static it.lbsoftware.daily.templates.TemplateUtils.hasNonNullParameter;
import static it.lbsoftware.daily.templates.TemplateUtils.redirectIfAuthenticated;

import it.lbsoftware.daily.config.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = Constants.LOGIN_PATH)
@RequiredArgsConstructor
@CommonsLog
class AppUserLoginController {

  @GetMapping
  public String login(Authentication authentication, HttpServletRequest request, Model model) {
    if (hasNonNullParameter(request, DefaultLoginPageGeneratingFilter.ERROR_PARAMETER_NAME)) {
      // Here, we are currently handling /login?error
      var errorMessage = getInvalidCredentialsErrorMessage(request);
      log.warn("Login: " + errorMessage);
      model.addAttribute(Constants.INVALID_CREDENTIALS_ERROR, errorMessage);
    }
    return redirectIfAuthenticated(authentication).orElse(Constants.LOGIN_VIEW);
  }
}
