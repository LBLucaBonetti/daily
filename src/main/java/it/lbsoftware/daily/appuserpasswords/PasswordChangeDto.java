package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.config.Constants;
import jakarta.validation.constraints.NotBlank;

/**
 * Dto used to change the password.
 *
 * @param oldPassword The old password to confirm the ownership of the user
 * @param newPassword The new password
 * @param newPasswordConfirmation The new password again, to confirm the decision
 */
public record PasswordChangeDto(
    @NotBlank(message = Constants.NOT_BLANK_MESSAGE) String oldPassword,
    @NotBlank(message = Constants.NOT_BLANK_MESSAGE) String newPassword,
    @NotBlank(message = Constants.NOT_BLANK_MESSAGE) String newPasswordConfirmation) {

  /**
   * Creates a {@link String} representation of this dto, filtering out credentials to prevent
   * logging them out.
   *
   * @return A {@link String} representation of this dto
   */
  @Override
  public String toString() {
    return "%s [oldPassword=***, newPassword=***, newPasswordConfirmation=***]"
        .formatted(this.getClass().getSimpleName());
  }
}
