package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.appusers.AppUser;

/**
 * Dto to change {@link AppUser} password.
 *
 * @param appUserEmail The e-mail of the {@link AppUser}
 * @param appUserFirstName The first name of the {@link AppUser}
 */
public record AppUserPasswordChangedDto(String appUserEmail, String appUserFirstName) {

  /**
   * Creates new dto gathering data from the provided parameter.
   *
   * @param appUser The source of the data
   */
  public AppUserPasswordChangedDto(final AppUser appUser) {
    this(appUser.getEmail(), appUser.getFirstName());
  }
}
