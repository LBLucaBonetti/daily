package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.config.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
  private String email;

  @NotBlank private String password;
  @NotBlank private String passwordConfirmation;

  @Size(max = Constants.APP_USER_FIRST_NAME_MAX)
  @NotBlank
  private String firstName;

  @Size(max = Constants.APP_USER_LAST_NAME_MAX)
  @NotBlank
  private String lastName;

  public String getEmail() {
    return StringUtils.lowerCase(this.email);
  }

  public void setEmail(String email) {
    this.email = StringUtils.lowerCase(email);
  }
}
