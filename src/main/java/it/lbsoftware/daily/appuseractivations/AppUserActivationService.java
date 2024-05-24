package it.lbsoftware.daily.appuseractivations;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.Optional;
import java.util.UUID;

/** Service to deal with app user activations. */
public interface AppUserActivationService {

  /**
   * Creates a new activation for the {@link AppUser} if the AuthProvider is DAILY; it provides a
   * unique activationCode that needs to be used for confirming the account and let it log in. The
   * activation has an expiration after which it will be considered invalid.
   *
   * @param appUser The {@link AppUser} to associate with the activation being created
   * @return The created {@link AppUserActivation} or empty if it is not created
   */
  Optional<AppUserActivation> createAppUserActivation(AppUser appUser);

  /**
   * Sets an {@link AppUserActivation} to activated now if it is not activated and is still valid
   * for being activated.
   *
   * @param activationCode Activation code of the {@link AppUserActivation}
   * @return True if the {@link AppUserActivation} is successfully activated, false otherwise
   */
  boolean setNonActivatedAndStillValidAppUserActivationActivated(UUID activationCode);

  /**
   * Creates the complete URI of the activation URI based on the provided activation code.
   *
   * @param activationCode The activation code to create the activation URI with
   * @return The complete URI of the activation URI, ready to be sent to the user
   */
  String getActivationUri(UUID activationCode);
}
