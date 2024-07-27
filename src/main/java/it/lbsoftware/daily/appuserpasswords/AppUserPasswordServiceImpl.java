package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.config.Constants.EMAIL_APP_USER_PASSWORD_RESET_CONFIRMATION_PATH;
import static it.lbsoftware.daily.config.Constants.EMAIL_APP_USER_PASSWORD_RESET_CONFIRMATION_SUBJECT;
import static it.lbsoftware.daily.config.Constants.EMAIL_APP_USER_PASSWORD_RESET_PATH;
import static it.lbsoftware.daily.config.Constants.EMAIL_APP_USER_PASSWORD_RESET_SUBJECT;
import static it.lbsoftware.daily.config.Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS;
import static it.lbsoftware.daily.config.Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS_MESSAGE;
import static it.lbsoftware.daily.config.Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserUtils;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.config.DailyConfig;
import it.lbsoftware.daily.emails.EmailInfo;
import it.lbsoftware.daily.emails.EmailService;
import it.lbsoftware.daily.frontend.OperationResult;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Main {@link it.lbsoftware.daily.appusers.AppUser} password service implementation, defining
 * methods that are not delegated to the framework (Spring Security or similar). Also handles {@link
 * AppUserPasswordReset} but delegates main entity operations and logic to {@link
 * AppUserPasswordModificationService}.
 */
@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserPasswordServiceImpl implements AppUserPasswordService {

  private final AppUserPasswordModificationService appUserPasswordModificationService;
  private final EmailService emailService;
  private final DailyConfig dailyConfig;

  @Override
  public void sendPasswordResetNotification(
      @NonNull PasswordResetNotificationDto passwordResetNotificationDto, @NonNull Model model) {
    var email = passwordResetNotificationDto.getEmail();
    appUserPasswordModificationService
        .createAppUserPasswordReset(email)
        .ifPresentOrElse(
            this::sendPasswordResetEmail,
            () ->
                log.warn(
                    ("Could not create AppUserPasswordReset for AppUser with email %s; either the "
                            + "AppUser does not exist, or there is already another "
                            + "AppUserPasswordReset for it or the save failed")
                        .formatted(email)));
    // Show success anyway to avoid leaking information
    model.addAttribute(
        PASSWORD_RESET_NOTIFICATION_SUCCESS, PASSWORD_RESET_NOTIFICATION_SUCCESS_MESSAGE);
  }

  @Override
  public OperationResult resetPassword(PasswordResetDto passwordResetDto) {
    try {
      var appUserPasswordResetDto =
          appUserPasswordModificationService.resetAppUserPassword(passwordResetDto).orElseThrow();
      sendPasswordResetConfirmationEmail(
          AppUser.builder()
              .email(appUserPasswordResetDto.getAppUserEmail())
              .firstName(appUserPasswordResetDto.getAppUserFirstName())
              .build());
      return OperationResult.ok();
    } catch (CompromisedPasswordException e) {
      return OperationResult.error(
          Constants.PASSWORD_RESET_CODE_FAILURE,
          "Your new password is compromised and should not be used. Please choose another one");
    } catch (Exception e) {
      return OperationResult.error(
          Constants.PASSWORD_RESET_CODE_FAILURE, "Invalid password reset code");
    }
  }

  @Override
  public OperationResult changePassword(PasswordChangeDto passwordChangeDto, AppUser appUser) {
    try {
      var appUserPasswordChangedDto =
          appUserPasswordModificationService
              .changeAppUserPassword(passwordChangeDto, appUser)
              .orElseThrow();
      sendPasswordResetConfirmationEmail(
          AppUser.builder()
              .email(appUserPasswordChangedDto.appUserEmail())
              .firstName(appUserPasswordChangedDto.appUserFirstName())
              .build());
      return OperationResult.ok();
    } catch (CompromisedPasswordException e) {
      return OperationResult.error(
          Constants.ERROR_KEY, Constants.ERROR_PASSWORD_CHANGE_COMPROMISED);
    } catch (Exception e) {
      return OperationResult.error(Constants.ERROR_KEY, Constants.ERROR_PASSWORD_CHANGE_GENERIC);
    }
  }

  /**
   * Sends a password reset confirmation asynchronously. Since the error handling is not important
   * for the app user to avoid leaking information, the async operation is fine here.
   *
   * @param appUser Contains data used to send this e-mail
   */
  private void sendPasswordResetConfirmationEmail(final AppUser appUser) {
    emailService.sendAsynchronously(
        new EmailInfo(
            EMAIL_APP_USER_PASSWORD_RESET_CONFIRMATION_PATH,
            appUser.getEmail(),
            EMAIL_APP_USER_PASSWORD_RESET_CONFIRMATION_SUBJECT),
        Map.of("appUserFirstName", AppUserUtils.getFirstNameOrDefault(appUser)));
  }

  /**
   * Sends a password reset notification asynchronously. Since the error handling is not important
   * for the app user to avoid leaking information, the async operation is fine here.
   *
   * @param appUserPasswordResetDto Contains data used to send this e-mail
   */
  private void sendPasswordResetEmail(final AppUserPasswordResetDto appUserPasswordResetDto) {
    emailService.sendAsynchronously(
        new EmailInfo(
            EMAIL_APP_USER_PASSWORD_RESET_PATH,
            appUserPasswordResetDto.getAppUserEmail(),
            EMAIL_APP_USER_PASSWORD_RESET_SUBJECT),
        Map.of(
            "appUserFirstName",
            AppUserUtils.getFirstNameOrDefault(
                AppUser.builder().firstName(appUserPasswordResetDto.getAppUserFirstName()).build()),
            "passwordResetUri",
            getPasswordResetUri(appUserPasswordResetDto.getPasswordResetCode()),
            "minutesBeforeExpiration",
            PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES));
  }

  private String getPasswordResetUri(final UUID passwordResetCode) {
    return Optional.ofNullable(passwordResetCode)
        .map(
            (UUID appUserPasswordResetCode) ->
                UriComponentsBuilder.fromUriString(dailyConfig.getBaseUri())
                    .pathSegment(Constants.PASSWORD_RESET_VIEW)
                    .queryParam(Constants.PASSWORD_RESET_CODE, appUserPasswordResetCode.toString())
                    .build()
                    .toUriString())
        .orElseThrow(
            () -> new IllegalArgumentException("The provided password reset code is malformed"));
  }
}
