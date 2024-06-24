package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appuserpasswords.AppUserPasswordResetDto;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.config.Constants;
import java.util.Optional;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;

/** Utilities for {@link AppUser} entities. */
@CommonsLog
public final class AppUserUtils {

  private AppUserUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  /**
   * Determines whether the supplier auth provider is OAuth2; an app user is either an OAuth2 or a
   * daily app user.
   *
   * @param authProvider The auth provider to check
   * @return True if the auth provider is OAuth2, false otherwise
   */
  public static boolean isOauth2AuthProvider(final AuthProvider authProvider) {
    return !AuthProvider.DAILY.equals(authProvider);
  }

  /**
   * Determines whether the supplier auth provider is daily; an app user is either an OAuth2 or a
   * daily app user.
   *
   * @param authProvider The auth provider to check
   * @return True if the auth provider is daily, false otherwise
   */
  public static boolean isDailyAuthProvider(final AuthProvider authProvider) {
    return !isOauth2AuthProvider(authProvider);
  }

  /**
   * Retrieves the app user first name or a default one if missing.
   *
   * @param appUser The source app user
   * @return The app user first name or a default one if missing
   */
  public static String getFirstNameOrDefault(final AppUser appUser) {
    return Optional.ofNullable(appUser)
        .map(AppUser::getFirstName)
        .filter(StringUtils::isNotBlank)
        .orElse(Constants.APP_USER_UNSPECIFIED_NAME);
  }

  /**
   * Retrieves the app user first name or a default one if missing, getting data from the provided
   * parameter.
   *
   * @param appUserPasswordResetDto The source data
   * @return The app user first name or a default one if missing
   */
  public static String getFirstNameOrDefault(
      final AppUserPasswordResetDto appUserPasswordResetDto) {
    return Optional.ofNullable(appUserPasswordResetDto)
        .map(
            (var nonNullAppUserPasswordResetDto) ->
                AppUser.builder()
                    .email(nonNullAppUserPasswordResetDto.getAppUserEmail())
                    .firstName(nonNullAppUserPasswordResetDto.getAppUserFirstName())
                    .build())
        .map(AppUserUtils::getFirstNameOrDefault)
        .orElse(Constants.APP_USER_UNSPECIFIED_NAME);
  }
}
