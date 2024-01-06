package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.appusers.AppUser.AuthProvider.DAILY;
import static it.lbsoftware.daily.appusers.AppUser.AuthProvider.GOOGLE;

import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import lombok.NonNull;

public final class AppUserUtils {

  private AppUserUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  public static AuthProvider getOauth2AuthProvider(@NonNull final String email) {
    if (email.endsWith("@gmail.com")) {
      return GOOGLE;
    }
    return DAILY;
  }
}
