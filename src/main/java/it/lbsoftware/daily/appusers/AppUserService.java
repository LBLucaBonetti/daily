package it.lbsoftware.daily.appusers;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface AppUserService {

  /**
   * Retrieves the unique id of the user
   *
   * @param appUser The injected OidcUser containing the required information
   * @return The unique id of the user
   */
  String getUid(OidcUser appUser);
}
