package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.frontend.OperationResult;
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

  /**
   * Tries to reset the password with the given data.
   *
   * @param passwordResetDto The data to reset the password with; also contains the password reset
   *     code to identify the {@link it.lbsoftware.daily.appusers.AppUser}
   * @return A detailed result, optionally containing messages to show to the user
   */
  OperationResult resetPassword(PasswordResetDto passwordResetDto);

  /**
   * Tries to change the {@link it.lbsoftware.daily.appusers.AppUser} password with the given data.
   *
   * @param passwordChangeDto The data to change the password with
   * @param appUser The subject
   * @return A detailed result, optionally containing messages to show to the user
   */
  OperationResult changePassword(PasswordChangeDto passwordChangeDto, AppUser appUser);
}
