package it.lbsoftware.daily.appusersignup;

import static it.lbsoftware.daily.templates.TemplateUtils.redirectIfAuthenticated;

import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.config.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@CommonsLog
class AppUserSignupController {

  private static final String APP_USER_DTO_PARAMETER = "appUserDto";
  private final AppUserSignupService appUserSignupService;

  @GetMapping(path = Constants.SIGNUP_PATH)
  public String signup(Model model, Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              model.addAttribute(APP_USER_DTO_PARAMETER, new AppUserDto());
              return Constants.SIGNUP_VIEW;
            });
  }

  @PostMapping(path = Constants.SIGNUP_PATH)
  public String signup(
      @ModelAttribute(APP_USER_DTO_PARAMETER) @Valid AppUserDto appUserDto,
      BindingResult result,
      Model model,
      Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(() -> appUserSignupService.signup(appUserDto, result, model));
  }
}
