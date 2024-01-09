package it.lbsoftware.daily.appusers;

public interface AppUserService {

  /**
   * Retrieves the information to display for the current principal
   *
   * @param principal The current principal, either from Daily or OAuth2/OIDC
   * @return Information about the current principal
   */
  InfoDto getAppUserInfo(Object principal);

  /**
   * Retrieves the appUser
   *
   * @param principal The injected principal containing the required information
   * @return The unique appUser in the daily realm
   */
  AppUser getAppUser(Object principal);
}
