package it.lbsoftware.daily.appuserpasswords;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.stereotype.Service;

/** Deals with password security, such as complexity, whether it is compromised, ... */
@Service
@RequiredArgsConstructor
public class AppUserPasswordSecurityService {

  private final CompromisedPasswordChecker compromisedPasswordChecker;

  /**
   * Checks the security of the cleartext password passed in.
   *
   * @param cleartextPassword The cleartext password to check
   * @throws IllegalArgumentException When the password is blank
   * @throws CompromisedPasswordException When the password is compromised
   */
  public void check(final String cleartextPassword) {
    // The password should not be blank
    if (StringUtils.isBlank(cleartextPassword)) {
      throw new IllegalArgumentException("The chosen password is blank");
    }
    // The password should not be compromised
    if (isCompromised(cleartextPassword)) {
      throw new CompromisedPasswordException("The chosen password is compromised");
    }
  }

  private boolean isCompromised(final String cleartextPassword) {
    return compromisedPasswordChecker.check(cleartextPassword).isCompromised();
  }
}
