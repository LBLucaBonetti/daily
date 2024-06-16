package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.templates.TemplateUtils.redirectIfAuthenticated;

import it.lbsoftware.daily.config.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = Constants.PASSWORD_PATH)
@RequiredArgsConstructor
@CommonsLog
class AppUserPasswordController {

  private static final String PASSWORD_RESET_LINK_DTO_PARAMETER = "passwordResetLinkDto";

  @GetMapping
  public String sendPasswordResetLink(Model model, Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              model.addAttribute(
                  PASSWORD_RESET_LINK_DTO_PARAMETER, new PasswordResetLinkDto(StringUtils.EMPTY));
              return Constants.SEND_PASSWORD_RESET_LINK_VIEW;
            });
  }

  @PostMapping
  public String sendPasswordResetLink(
      @ModelAttribute(PASSWORD_RESET_LINK_DTO_PARAMETER) @Valid
          PasswordResetLinkDto passwordResetLinkDto,
      BindingResult result,
      Model model,
      Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              return Constants.SEND_PASSWORD_RESET_LINK_VIEW;
            });
  }
}
