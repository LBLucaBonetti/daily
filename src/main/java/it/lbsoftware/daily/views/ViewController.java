package it.lbsoftware.daily.views;

import static it.lbsoftware.daily.config.Constants.REDIRECT;

import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.config.Constants;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@CommonsLog
class ViewController {

  private static final String APP_USER_DTO_PARAMETER = "appUserDto";
  private final AppUserService appUserService;

  @GetMapping(path = Constants.SIGNUP_PATH)
  public String signup(Model model, Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              model.addAttribute(APP_USER_DTO_PARAMETER, new AppUserDto());
              return Constants.SIGNUP_VIEW;
            });
  }

  @GetMapping(path = Constants.LOGIN_PATH)
  public String login(Authentication authentication) {
    return redirectIfAuthenticated(authentication).orElse(Constants.LOGIN_VIEW);
  }

  @PostMapping(path = Constants.SIGNUP_PATH)
  public String signup(
      @ModelAttribute(APP_USER_DTO_PARAMETER) @Valid AppUserDto appUserDto,
      BindingResult result,
      Model model,
      Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(() -> appUserService.signup(appUserDto, result, model));
  }

  @GetMapping(value = Constants.ACTIVATION_PATH)
  public String activate(
      @PathVariable(Constants.ACTIVATION_CODE) UUID activationCode,
      Authentication authentication,
      Model model) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              if (appUserService.activate(activationCode)) {
                model.addAttribute(
                    "activationCodeSuccess", "Your account has been activated! You can now log in");
              } else {
                log.info("Found an invalid activation code " + activationCode);
                model.addAttribute("activationCodeFailure", "Invalid activation code");
              }
              return Constants.LOGIN_VIEW;
            });
  }

  private Optional<String> redirectIfAuthenticated(final Authentication authentication) {
    return Optional.ofNullable(authentication).map(auth -> REDIRECT);
  }
}
