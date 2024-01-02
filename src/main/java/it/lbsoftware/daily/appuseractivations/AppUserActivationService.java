package it.lbsoftware.daily.appuseractivations;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.Optional;
import java.util.UUID;

public interface AppUserActivationService {

  /**
   * Creates a new activation for the appUser if the AuthProvider is DAILY; it provides a unique
   * activationCode that needs to be used for confirming the account and let the appUser log in. The
   * activation has an expiration after which it will be considered invalid
   *
   * @param appUser The appUser to associate with the activation being created
   * @return The created AppUserActivation or empty if it is not created
   */
  Optional<AppUserActivation> createAppUserActivation(AppUser appUser);

  /**
   * Reads an appUserActivation
   *
   * @param activationCode Activation code of the appUserActivation
   * @return Read appUserActivation or empty value
   */
  Optional<AppUserActivation> readAppUserActivation(UUID activationCode);

  /**
   * Sets the appUserActivation as activated now
   *
   * @param appUserActivation The appUserActivation to activate
   */
  void setActivated(AppUserActivation appUserActivation);

  /**
   * Returns whether the appUserActivation is activated
   *
   * @param appUserActivation The AppUserActivation to check
   * @return True if the AppUserActivation is activated or false otherwise
   */
  boolean isActivated(AppUserActivation appUserActivation);

  /**
   * Returns whether the appUserActivation is valid
   *
   * @param appUserActivation The AppUserActivation to check
   * @return True if the AppUserActivation is valid or false otherwise
   */
  boolean isValid(AppUserActivation appUserActivation);
}
