package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appusers.AppUser.AuthProvider;

/**
 * Contains the app user information to display in the frontend.
 *
 * @param fullName The full name of the app user
 * @param email The e-mail of the app user
 * @param authProvider The auth provider; daily app users and OAuth2 app users have different
 *     permissions (such as the password change feature that makes no sense for OAuth2 app users)
 */
public record InfoDto(String fullName, String email, AuthProvider authProvider) {}
