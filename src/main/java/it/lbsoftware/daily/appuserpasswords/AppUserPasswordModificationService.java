package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.appusers.AppUserUtils.isDailyAuthProvider;
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
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Main {@link AppUserPasswordReset} service implementation. Also handles change operations. */
@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserPasswordModificationService {

  private final AppUserPasswordResetRepository appUserPasswordResetRepository;
  private final AppUserRepository appUserRepository;
  private final CompromisedPasswordChecker compromisedPasswordChecker;
  private final PasswordEncoder passwordEncoder;

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
        .findByEmailIgnoreCaseAndAuthProviderAndEnabledTrue(appUserEmail, validAuthProvider)
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
    return findStillValidAppUserPasswordResetFetchEnabledAppUser(passwordResetCode)
        .map(AppUserPasswordResetDto::new);
  }

  private Optional<AppUserPasswordReset> findStillValidAppUserPasswordResetFetchEnabledAppUser(
      final UUID passwordResetCode) {
    return appUserPasswordResetRepository.findStillValidAppUserPasswordResetFetchEnabledAppUser(
        passwordResetCode, LocalDateTime.now());
  }

  /**
   * Tries to reset the {@link AppUser} password. Performs additional checks but supposes the
   * provided pair already matches.
   *
   * @param passwordResetDto Contains the data to set the new password
   * @return A dto of the {@link AppUserPasswordReset} when found
   * @throws java.util.NoSuchElementException When the {@link AppUserPasswordReset} or the {@link
   *     AppUser} are not valid or found
   * @throws CompromisedPasswordException When the new password is compromised
   */
  @Transactional
  public Optional<AppUserPasswordResetDto> resetAppUserPassword(
      @NonNull final PasswordResetDto passwordResetDto) {
    // Still valid password reset should exist
    var appUserPasswordReset =
        findStillValidAppUserPasswordResetFetchEnabledAppUser(
                passwordResetDto.getPasswordResetCode())
            .orElseThrow();
    var newPassword = passwordResetDto.getPassword();
    var appUser = appUserPasswordReset.getAppUser();
    changeAppUserPassword(newPassword, appUser);
    appUserPasswordReset.setUsedAt(LocalDateTime.now());
    return Optional.of(new AppUserPasswordResetDto(appUserPasswordReset, appUser));
  }

  /**
   * Tries to change the {@link AppUser} password with the provided new one, performing additional
   * checks and throwing on errors.
   *
   * @param newCleartextPassword The new and non-encoded (or cleartext) password
   * @param appUser The subject
   */
  private void changeAppUserPassword(final String newCleartextPassword, final AppUser appUser) {
    // New password should not be compromised
    if (compromisedPasswordChecker.check(newCleartextPassword).isCompromised()) {
      throw new CompromisedPasswordException("The chosen password is compromised");
    }
    // Encode and save the new password
    appUser.setPassword(passwordEncoder.encode(newCleartextPassword));
    log.info("The AppUser with e-mail %s is changing its password".formatted(appUser.getEmail()));
  }

  /**
   * Tries to change the {@link AppUser} password. Performs additional checks but supposes the
   * provided pair already matches.
   *
   * @param passwordChangeDto Contains the data to set the new password
   * @param appUser The subject
   * @return A dto of the {@link AppUser} when the operation succeeds
   * @throws CompromisedPasswordException When the new password is compromised
   */
  @Transactional
  public Optional<AppUserPasswordChangedDto> changeAppUserPassword(
      @NonNull final PasswordChangeDto passwordChangeDto, @NonNull final AppUser appUser) {
    if (!isDailyAuthProvider(appUser.getAuthProvider())) {
      log.warn(
          ("The non-daily AppUser with e-mail %s tried to change its password; non-daily AppUser "
                  + "instances do not store passwords")
              .formatted(appUser.getEmail()));
      return Optional.empty();
    }
    var oldPassword = passwordChangeDto.oldPassword();
    if (!passwordEncoder.matches(oldPassword, appUser.getPassword())) {
      log.warn(
          ("The AppUser with e-mail %s tried to change its password but provided a wrong old "
                  + "password")
              .formatted(appUser.getEmail()));
      return Optional.empty();
    }
    var newPassword = passwordChangeDto.newPassword();
    changeAppUserPassword(newPassword, appUser);
    // The following is needed because the appUser has been fetched from another transaction and
    // passed as a method parameter to this one, so it is not managed by the current method
    // transaction; if we avoid the save operation, the appUser will not be updated. Lesson to the
    // future myself: NEVER EVER pass entities around, use DTOs for everything and fetch data inside
    // transactional methods to be safe. Lesson #2 to the future myself: if the AppUser entity
    // fetched in the other transaction is modified by the time we call the following save, we could
    // potentially lose the information if the persistence provider does not perform a merge
    // operation internally
    var updatedAppUser = appUserRepository.save(appUser);
    return Optional.of(new AppUserPasswordChangedDto(updatedAppUser));
  }
}
