package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.config.Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusers.AppUserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Main {@link AppUserPasswordReset} service implementation. */
@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserPasswordResetService {

  private final AppUserPasswordResetRepository appUserPasswordResetRepository;
  private final AppUserRepository appUserRepository;

  /**
   * Tries to find the {@link it.lbsoftware.daily.appusers.AppUser} and create a new {@link
   * AppUserPasswordReset} for it. Since OAuth2 app users do not provide their password, only daily
   * app users are allowed to reset their password.
   *
   * @param appUserEmail The e-mail of the {@link it.lbsoftware.daily.appusers.AppUser}
   * @return When the operation is successful, the data that needs to be sent via e-mail to the
   *     user; an empty value on failure
   */
  @Transactional
  public Optional<AppUserPasswordResetDto> createAppUserPasswordReset(
      @NonNull final String appUserEmail) {
    var validAuthProvider = AuthProvider.DAILY;
    return appUserRepository
        .findByEmailIgnoreCaseAndAuthProvider(appUserEmail, validAuthProvider)
        .filter(this::notExistsAppUserPasswordReset)
        .map(this::createAppUserPasswordReset)
        .map(AppUserPasswordResetDto::new);
  }

  private AppUserPasswordReset createAppUserPasswordReset(final AppUser appUser) {
    return appUserPasswordResetRepository.save(
        AppUserPasswordReset.builder()
            .appUser(appUser)
            .passwordResetCode(UUID.randomUUID())
            .expiredAt(
                LocalDateTime.now().plusMinutes(PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES))
            .usedAt(null) // Not used yet
            .build());
  }

  private boolean notExistsAppUserPasswordReset(final AppUser appUser) {
    return appUserPasswordResetRepository.findByAppUser(appUser).isEmpty();
  }

  /**
   * Finds an {@link AppUserPasswordReset} that is still valid by its password reset code.
   *
   * @param passwordResetCode The password reset code to find the {@link AppUserPasswordReset} by
   * @return The {@link AppUserPasswordResetDto} created from the {@link AppUserPasswordReset}
   *     entity or an empty value
   */
  @Transactional(readOnly = true)
  public Optional<AppUserPasswordResetDto> findStillValidAppUserPasswordReset(
      @NonNull final UUID passwordResetCode) {
    return appUserPasswordResetRepository
        .findStillValidAppUserPasswordResetFetchEnabledAppUser(
            passwordResetCode, LocalDateTime.now())
        .map(AppUserPasswordResetDto::new);
  }
}
