package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.config.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
public class AppUserDto {

  @Size(max = Constants.APP_USER_EMAIL_MAX)
  @Email
  @NotNull
  @NotBlank(
      message =
          Constants
              .NOT_BLANK_MESSAGE) // This is only used to produce the same invalid message the other
  // fields produce; it is
  // not strictly required since the @Email annotation would not accept a blank string
  // anyway
  private String email;

  @NotBlank(message = Constants.NOT_BLANK_MESSAGE)
  private String password;

  @NotBlank(message = Constants.NOT_BLANK_MESSAGE)
  private String passwordConfirmation;

  @Size(max = Constants.APP_USER_FIRST_NAME_MAX)
  @NotBlank(message = Constants.NOT_BLANK_MESSAGE)
  private String firstName;

  @Size(max = Constants.APP_USER_LAST_NAME_MAX)
  @NotBlank(message = Constants.NOT_BLANK_MESSAGE)
  private String lastName;

  @NotNull
  @NotBlank(message = Constants.NOT_BLANK_MESSAGE)
  @Pattern(
      regexp = Constants.APP_USER_LANG_REGEXP,
      message = Constants.APP_USER_LANG_PATTERN_MESSAGE)
  private String lang = "en";

  public String getEmail() {
    return StringUtils.toRootLowerCase(this.email);
  }

  public void setEmail(String email) {
    this.email = StringUtils.toRootLowerCase(email);
  }
}
