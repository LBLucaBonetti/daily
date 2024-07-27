package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.frontend.TemplateUtils.addErrorToView;
import static it.lbsoftware.daily.frontend.TemplateUtils.redirectIfAuthenticated;

import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
import jakarta.validation.Valid;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@CommonsLog
class AppUserPasswordController {

  private static final String PASSWORD_RESET_NOTIFICATION_DTO_PARAMETER =
      "passwordResetNotificationDto";
  private static final String PASSWORD_RESET_DTO_PARAMETER = "passwordResetDto";
  private final AppUserPasswordService appUserPasswordService;
  private final AppUserPasswordModificationService appUserPasswordModificationService;
  private final AppUserService appUserService;

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
              appUserPasswordModificationService
                  .findStillValidAppUserPasswordReset(code)
                  .ifPresentOrElse(
                      (var appUserPasswordResetDto) -> passwordResetDto.setPasswordResetCode(code),
                      () -> {
                        log.info(
                            "Found an invalid password reset code "
                                + code
                                + " trying to get reset password view");
                        model.addAttribute(
                            Constants.PASSWORD_RESET_CODE_FAILURE, "Invalid password reset code");
                      });

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
    return redirectIfAuthenticated(authentication)
        .orElseGet(
            () -> {
              // Show validation errors if any
              if (bindingResult.hasErrors()) {
                return Constants.PASSWORD_RESET_VIEW;
              }
              // Passwords should match
              if (!Objects.equals(
                  passwordResetDto.getPassword(), passwordResetDto.getPasswordConfirmation())) {
                addErrorToView(bindingResult, "Passwords should match");
                return Constants.PASSWORD_RESET_VIEW;
              }
              var resetPasswordResult = appUserPasswordService.resetPassword(passwordResetDto);
              if (resetPasswordResult.isOk()) {
                log.info(
                    "Password successfully reset for code "
                        + passwordResetDto.getPasswordResetCode());
                model.addAttribute(
                    Constants.PASSWORD_RESET_CODE_SUCCESS,
                    "Your password has been successfully reset! You can now log in");
                return Constants.LOGIN_VIEW;
              }
              log.info(
                  "Found an invalid reset code "
                      + passwordResetDto.getPasswordResetCode()
                      + " trying to post reset password");
              resetPasswordResult.ifHasMessage(model::addAttribute);
              return Constants.PASSWORD_RESET_VIEW;
            });
  }

  @PutMapping(Constants.APP_USER_PATH + "/passwords")
  @ResponseBody
  public ResponseEntity<Void> changePassword(
      @Valid @RequestBody PasswordChangeDto passwordChangeDto,
      @AuthenticationPrincipal Object principal) {
    if (!Objects.equals(
        passwordChangeDto.newPassword(), passwordChangeDto.newPasswordConfirmation())) {
      throw new DailyBadRequestException(Constants.ERROR_PASSWORD_CHANGE_MISMATCH);
    }
    var changePasswordResult =
        appUserPasswordService.changePassword(
            passwordChangeDto, appUserService.getAppUser(principal));
    if (changePasswordResult.isError()) {
      changePasswordResult.ifHasMessage(
          (String messageKey, String messageValue) -> {
            throw new DailyBadRequestException(messageValue);
          });
      throw new DailyBadRequestException(null);
    }
    return ResponseEntity.noContent().build();
  }
}
