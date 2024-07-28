package it.lbsoftware.daily.appuseractivations;

import static it.lbsoftware.daily.frontend.TemplateUtils.redirectIfAuthenticated;

import it.lbsoftware.daily.config.Constants;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = Constants.ACTIVATION_PATH)
@RequiredArgsConstructor
@CommonsLog
class AppUserActivationController {

  private final AppUserActivationService appUserActivationService;

  @GetMapping
  public String activate(
      @PathVariable(Constants.ACTIVATION_CODE) UUID activationCode,
      Model model,
      Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              if (appUserActivationService.setNonActivatedAndStillValidAppUserActivationActivated(
                  activationCode)) {
                model.addAttribute(
                    Constants.ACTIVATION_CODE_SUCCESS,
                    "Your account has been activated! You can now log in");
                log.info("Activation code " + activationCode + " has been activated");
              } else {
                log.info("Found an invalid activation code " + activationCode);
                model.addAttribute(Constants.ACTIVATION_CODE_FAILURE, "Invalid activation code");
              }
              return Constants.LOGIN_VIEW;
            });
  }
}
