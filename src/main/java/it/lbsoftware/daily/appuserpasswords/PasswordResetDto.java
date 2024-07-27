package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.config.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Dto used to reset the password. */
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PasswordResetDto {

  @NotBlank(message = Constants.NOT_BLANK_MESSAGE)
  @ToString.Exclude
  private String password;

  @NotBlank(message = Constants.NOT_BLANK_MESSAGE)
  @ToString.Exclude
  private String passwordConfirmation;

  @NotNull private UUID passwordResetCode;
}
