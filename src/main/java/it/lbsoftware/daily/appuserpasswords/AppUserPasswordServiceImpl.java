package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.config.Constants.EMAIL_APP_USER_PASSWORD_RESET_PATH;
import static it.lbsoftware.daily.config.Constants.EMAIL_APP_USER_PASSWORD_RESET_SUBJECT;
import static it.lbsoftware.daily.config.Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS;
import static it.lbsoftware.daily.config.Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS_MESSAGE;
import static it.lbsoftware.daily.config.Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserUtils;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.config.DailyConfig;
import it.lbsoftware.daily.emails.EmailInfo;
import it.lbsoftware.daily.emails.EmailService;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Main {@link it.lbsoftware.daily.appusers.AppUser} password reset service implementation. Also
 * handles {@link AppUserPasswordReset}.
 */
@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserPasswordServiceImpl implements AppUserPasswordService {

  private final AppUserPasswordResetRepository appUserPasswordResetRepository;
  private final AppUserRepository appUserRepository;
  private final EmailService emailService;
  private final DailyConfig dailyConfig;

  @Override
  @Transactional
  public void sendPasswordResetNotification(
      @NonNull PasswordResetNotificationDto passwordResetNotificationDto, @NonNull Model model) {
    // 1. Find the app user; only DAILY app users provided a
    // password, so ignore the other types
    var email = passwordResetNotificationDto.getEmail();
    var validAuthProvider = AuthProvider.DAILY;
    appUserRepository
        .findByEmailIgnoreCaseAndAuthProvider(email, validAuthProvider)
        .ifPresent(
            (var appUser) ->
                // 2. Try to create the password reset for the app user; ignore any failure
                createAppUserPasswordReset(appUser)
                    .ifPresent(
                        (var appUserPasswordReset) ->
                            // 3. Send the password reset notification; it the operation fails, an
                            // exception will be thrown so that the transaction will be rolled back
                            sendPasswordResetNotification(
                                appUser, appUserPasswordReset.getPasswordResetCode())));
    // 4. Show success anyway to avoid leaking information
    model.addAttribute(
        PASSWORD_RESET_NOTIFICATION_SUCCESS, PASSWORD_RESET_NOTIFICATION_SUCCESS_MESSAGE);
  }

  /**
   * Sends a password reset notification synchronously. Does not catch exceptions because when this
   * operation fails, a rollback of the transaction should be performed (it makes no sense to create
   * an AppUserPasswordReset when the e-mail for it could not be sent).
   *
   * @param appUser The app user to send this notification to
   * @param passwordResetCode The password reset code
   */
  private void sendPasswordResetNotification(final AppUser appUser, final UUID passwordResetCode) {
    emailService.sendSynchronously(
        new EmailInfo(
            EMAIL_APP_USER_PASSWORD_RESET_PATH,
            appUser.getEmail(),
            EMAIL_APP_USER_PASSWORD_RESET_SUBJECT),
        Map.of(
            "appUserFirstName",
            AppUserUtils.getFirstNameOrDefault(appUser),
            "passwordResetUri",
            getPasswordResetUri(passwordResetCode),
            "minutesBeforeExpiration",
            PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES));
  }

  private Optional<AppUserPasswordReset> createAppUserPasswordReset(final AppUser appUser) {
    try {
      return Optional.of(
          appUserPasswordResetRepository.save(
              AppUserPasswordReset.builder()
                  .appUser(appUser)
                  .passwordResetCode(UUID.randomUUID())
                  .expiredAt(
                      LocalDateTime.now()
                          .plusMinutes(PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES))
                  .build()));
    } catch (Exception e) {
      log.error("Could not save AppUserPasswordReset", e);
    }
    return Optional.empty();
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
