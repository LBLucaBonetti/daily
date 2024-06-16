package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.config.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/** Dto used to send data when requesting a password reset link. */
@EqualsAndHashCode
public class PasswordResetLinkDto {

  @Size(max = Constants.APP_USER_EMAIL_MAX)
  @Email
  @NotNull
  @NotBlank(
      message =
          Constants
              .NOT_BLANK_MESSAGE) // This is only used to produce the same invalid message the other
  // fields produce; it is not strictly required since the @Email annotation would not accept a
  // blank string anyway
  private String email;

  public PasswordResetLinkDto(final String email) {
    setEmail(email);
  }

  public String getEmail() {
    return StringUtils.toRootLowerCase(this.email);
  }

  public void setEmail(String email) {
    this.email = StringUtils.toRootLowerCase(email);
  }
}
