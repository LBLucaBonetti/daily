package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.appusers.AppUser.AuthProvider.DAILY;
import static it.lbsoftware.daily.appusers.AppUser.AuthProvider.GOOGLE;

import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.config.Constants;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

public final class AppUserUtils {

  private AppUserUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  /**
   * Retrieves the {@code AuthProvider} from the provided e-mail address
   *
   * @param email The source e-mail address to retrieve the {@code AuthProvider} from
   * @return The {@code AuthProvider}
   */
  public static AuthProvider getAuthProvider(@NonNull final String email) {
    if (email.endsWith("@gmail.com")) {
      return GOOGLE;
    }
    return DAILY;
  }

  /**
   * Determines whether the supplier auth provider is OAuth2; an app user is either an OAuth2 or a
   * daily app user
   *
   * @param authProvider The auth provider to check
   * @return True if the auth provider is OAuth2, false otherwise
   */
  public static boolean isOauth2AuthProvider(final AuthProvider authProvider) {
    return !AuthProvider.DAILY.equals(authProvider);
  }

  /**
   * Determines whether the supplier auth provider is daily; an app user is either an OAuth2 or a
   * daily app user
   *
   * @param authProvider The auth provider to check
   * @return True if the auth provider is daily, false otherwise
   */
  public static boolean isDailyAuthProvider(final AuthProvider authProvider) {
    return !isOauth2AuthProvider(authProvider);
  }

  /**
   * Retrieves the app user first name or a default one if missing
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
}
