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
   * Creates a new AppUser with the provided information; its auth provider will be assigned
   * according to the authProvider parameter
   *
   * @param appUserDto The AppUser data
   * @param authProvider The auth provider to assign
   * @param authProviderId The unique identifier to distinct this OAuth2 user among the others in
   *     the specific OAuth2 domain; there should not be multiple users with the same authProviderId
   *     within that OAuth2 provider
   */
  void createOauth2AppUser(
      AppUserDto appUserDto, AppUser.AuthProvider authProvider, String authProviderId);

  /**
   * Activates an appUser
   *
   * @param activationCode The activation code provided
   * @return True if activated or false otherwise
   */
  boolean activate(UUID activationCode);
}
