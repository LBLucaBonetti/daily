package it.lbsoftware.daily.appuseractivations;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.UUID;

public final class AppUserActivationTestUtils {

  private AppUserActivationTestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * {@code AppUserActivation} generator; the created instance is not persisted
   *
   * @param activationCode Activation code
   * @param appUser The creator
   * @return The created {@code AppUserActivation}
   */
  public static AppUserActivation createAppUserActivation(
      final UUID activationCode, final AppUser appUser) {
    return AppUserActivation.builder().activationCode(activationCode).appUser(appUser).build();
  }
}
