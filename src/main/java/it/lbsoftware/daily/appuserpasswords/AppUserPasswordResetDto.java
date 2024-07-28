package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.appusers.AppUser;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

/** {@link AppUserPasswordReset} dto. */
@Getter
public class AppUserPasswordResetDto {

  private final String appUserEmail;
  private final String appUserFirstName;

  /**
   * This password should be the encoded one coming from the stored {@link AppUser}. No check is
   * performed here to verify it is not cleartext
   */
  private final String appUserEncodedPassword;

  private final UUID passwordResetCode;
  private final LocalDateTime expiredAt;

  /**
   * Creates a new dto with {@link AppUser} data as well as {@link AppUserPasswordReset} data. The
   * {@link AppUser} data is taken from the provided parameter if not null or from the {@link
   * AppUserPasswordReset}. Note that in the latter case the {@link AppUser} entity should have been
   * fetched.
   *
   * @param appUserPasswordReset The entity to convert to dto
   * @param appUser The (nullable) app user entity
   */
  public AppUserPasswordResetDto(
      @NonNull final AppUserPasswordReset appUserPasswordReset, final AppUser appUser) {
    if (appUser != null) {
      this.appUserEmail = appUser.getEmail();
      this.appUserFirstName = appUser.getFirstName();
      this.appUserEncodedPassword = appUser.getPassword();
    } else {
      var appUserPasswordResetAppUser = appUserPasswordReset.getAppUser();
      this.appUserEmail = appUserPasswordResetAppUser.getEmail();
      this.appUserFirstName = appUserPasswordResetAppUser.getFirstName();
      this.appUserEncodedPassword = appUserPasswordResetAppUser.getPassword();
    }
    this.passwordResetCode = appUserPasswordReset.getPasswordResetCode();
    this.expiredAt = appUserPasswordReset.getExpiredAt();
  }

  /**
   * Creates a new dto with {@link AppUser} data as well as {@link AppUserPasswordReset} data. The
   * {@link AppUser} data is taken from the {@link AppUserPasswordReset}. Note that the {@link
   * AppUser} entity should have been fetched.
   *
   * @param appUserPasswordReset The entity to convert to dto
   */
  public AppUserPasswordResetDto(final AppUserPasswordReset appUserPasswordReset) {
    this(appUserPasswordReset, null);
  }

  public String getAppUserEmail() {
    return StringUtils.toRootLowerCase(this.appUserEmail);
  }
}
