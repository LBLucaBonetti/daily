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
   * Sets an appUserActivation to activated now if it is not activated and is still valid for being
   * activated
   *
   * @param activationCode Activation code of the appUserActivation
   * @return True if the appUserActivation is successfully activated, false otherwise
   */
  boolean setNonActivatedAndStillValidAppUserActivationActivated(UUID activationCode);

  /**
   * Creates the complete URI of the activation URI based on the provided activation code
   *
   * @param activationCode The activation code to create the activation URI with
   * @return The complete URI of the activation URI, ready to be sent to the user
   * @implNote The caller should make sure an active request is being processed, so that a {@code
   *     RequestAttributes} can be found
   */
  String getActivationUri(String activationCode);
}
