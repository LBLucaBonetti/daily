package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.templates.TemplateUtils.redirectIfAuthenticated;

import it.lbsoftware.daily.config.Constants;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@CommonsLog
class AppUserPasswordController {

  private static final String PASSWORD_RESET_NOTIFICATION_DTO_PARAMETER =
      "passwordResetNotificationDto";
  private static final String PASSWORD_RESET_DTO_PARAMETER = "passwordResetDto";
  private final AppUserPasswordService appUserPasswordService;
  private final AppUserPasswordResetService appUserPasswordResetService;

  @GetMapping(value = Constants.SEND_PASSWORD_RESET_NOTIFICATION_PATH)
  public String sendPasswordResetNotification(Model model, Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              model.addAttribute(
                  PASSWORD_RESET_NOTIFICATION_DTO_PARAMETER, new PasswordResetNotificationDto());
              return Constants.SEND_PASSWORD_RESET_NOTIFICATION_VIEW;
            });
  }

  @PostMapping(Constants.SEND_PASSWORD_RESET_NOTIFICATION_PATH)
  public String sendPasswordResetNotification(
      @ModelAttribute(PASSWORD_RESET_NOTIFICATION_DTO_PARAMETER) @Valid
          PasswordResetNotificationDto passwordResetNotificationDto,
      BindingResult bindingResult,
      Model model,
      Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              // Show validation errors if any
              if (bindingResult.hasErrors()) {
                return Constants.SEND_PASSWORD_RESET_NOTIFICATION_VIEW;
              }
              appUserPasswordService.sendPasswordResetNotification(
                  passwordResetNotificationDto, model);
              return Constants.LOGIN_VIEW;
            });
  }

  @GetMapping(Constants.PASSWORD_RESET_PATH)
  public String resetPassword(
      @RequestParam(value = Constants.PASSWORD_RESET_CODE) UUID code,
      Model model,
      Authentication authentication) {
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              var passwordResetDto = new PasswordResetDto();
              appUserPasswordResetService
                  .findStillValidAppUserPasswordReset(code)
                  .ifPresentOrElse(
                      (var appUserPasswordResetDto) -> passwordResetDto.setPasswordResetCode(code),
                      () ->
                          model.addAttribute(
                              Constants.PASSWORD_RESET_CODE_FAILURE,
                              "Invalid password reset code"));

              model.addAttribute(PASSWORD_RESET_DTO_PARAMETER, passwordResetDto);
              return Constants.PASSWORD_RESET_VIEW;
            });
  }

  @PostMapping(Constants.PASSWORD_RESET_PATH)
  public String resetPassword(
      @ModelAttribute(PASSWORD_RESET_DTO_PARAMETER) @Valid PasswordResetDto passwordResetDto,
      BindingResult bindingResult,
      Model model,
      Authentication authentication) {
    return redirectIfAuthenticated(authentication).orElse(Constants.PASSWORD_RESET_VIEW);
  }
}
