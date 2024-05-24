package it.lbsoftware.daily.appuserevents;

/**
 * Contains the e-mail of the {@link it.lbsoftware.daily.appusers.AppUser} entity that just logged
 * in.
 *
 * @param email The e-mail address of the {@link it.lbsoftware.daily.appusers.AppUser} that just
 *     logged in
 */
public record AppUserLoginEvent(String email) {}
