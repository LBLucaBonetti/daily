package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.appuseractivations.AppUserActivation;
import it.lbsoftware.daily.appusers.AppUser;
import java.time.LocalDateTime;
import java.util.UUID;

public final class AppUserPasswordResetTestUtils {

  private AppUserPasswordResetTestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * {@link AppUserPasswordReset} generator; the created instance is not persisted.
   *
   * @param passwordResetCode Password reset code
   * @param expiredAt The expired at
   * @param appUser The creator
   * @return The created {@link AppUserActivation}
   */
  public static AppUserPasswordReset createAppUserPasswordReset(
      final UUID passwordResetCode, final LocalDateTime expiredAt, final AppUser appUser) {
    return AppUserPasswordReset.builder()
        .passwordResetCode(passwordResetCode)
        .expiredAt(expiredAt)
        .appUser(appUser)
        .build();
  }
}
