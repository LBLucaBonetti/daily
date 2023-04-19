package it.lbsoftware.daily.views;

import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.config.Constants;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
class ViewController {

  private static final String REDIRECT_HOME = "redirect:/";
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
      Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(() -> appUserService.signup(appUserDto, result));
  }

  private Optional<String> redirectIfAuthenticated(final Authentication authentication) {
    return Optional.ofNullable(authentication).map(auth -> REDIRECT_HOME);
  }
}
