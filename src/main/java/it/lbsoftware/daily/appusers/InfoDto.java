package it.lbsoftware.daily.appusers;

/**
 * Contains the app user information to display in the frontend.
 *
 * @param fullName The full name of the app user
 * @param email The e-mail of the app user
 */
public record InfoDto(String fullName, String email) {}
