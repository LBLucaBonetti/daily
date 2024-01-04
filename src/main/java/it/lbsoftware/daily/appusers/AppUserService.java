package it.lbsoftware.daily.appusers;

import java.util.UUID;

public interface AppUserService {

  /**
   * Retrieves the unique id of the user in the daily realm
   *
   * @param principal The injected principal containing the required information
   * @return The unique id of the user in the daily realm
   */
  UUID getUuid(Object principal);

  /**
   * Retrieves the information to display for the current principal
   *
   * @param principal The current principal, either from Daily or OAuth2/OIDC
   * @return Information about the current principal
   */
  InfoDto getAppUserInfo(Object principal);

  /**
   * Activates an appUser
   *
   * @param activationCode The activation code provided
   * @return True if activated or false otherwise
   */
  boolean activate(UUID activationCode);
}
