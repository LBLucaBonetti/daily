package it.lbsoftware.daily.appusersignups;

import static it.lbsoftware.daily.config.Constants.SIGNUP_VIEW;
import static it.lbsoftware.daily.frontend.TemplateUtils.redirectIfAuthenticated;

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
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = Constants.SIGNUP_PATH)
@RequiredArgsConstructor
@CommonsLog
class AppUserSignupController {

  private static final String APP_USER_DTO_PARAMETER = "appUserDto";
  private final AppUserSignupService appUserSignupService;

  @GetMapping
  public String signup(Model model, Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              model.addAttribute(APP_USER_DTO_PARAMETER, new AppUserDto());
              return SIGNUP_VIEW;
            });
  }

  @PostMapping
  public String signup(
      @ModelAttribute(APP_USER_DTO_PARAMETER) @Valid AppUserDto appUserDto,
      BindingResult bindingResult,
      Model model,
      Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              appUserSignupService.signup(appUserDto, bindingResult, model);
              return SIGNUP_VIEW;
            });
  }
}
