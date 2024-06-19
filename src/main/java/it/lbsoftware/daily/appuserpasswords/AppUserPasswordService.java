package it.lbsoftware.daily.appuserpasswords;

import org.springframework.ui.Model;

/**
 * Service to deal with {@link it.lbsoftware.daily.appusers.AppUser} password change/reset through
 * {@link AppUserPasswordReset}.
 */
public interface AppUserPasswordService {

  /**
   * Tries to send a password reset notification. This method should avoid leaking sensible
   * information that could lead to security vulnerabilities such as user enumeration.
   *
   * @param passwordResetNotificationDto Contains the details such as the {@link
   *     it.lbsoftware.daily.appusers.AppUser} e-mail address to send the notification to
   * @param model The ui model
   */
  void sendPasswordResetNotification(
      PasswordResetNotificationDto passwordResetNotificationDto, Model model);
}
