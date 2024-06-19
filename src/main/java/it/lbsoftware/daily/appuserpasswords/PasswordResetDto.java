package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.config.Constants;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dto used to reset the password. */
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetDto {

  @NotBlank(message = Constants.NOT_BLANK_MESSAGE)
  private String password;

  @NotBlank(message = Constants.NOT_BLANK_MESSAGE)
  private String passwordConfirmation;
}
