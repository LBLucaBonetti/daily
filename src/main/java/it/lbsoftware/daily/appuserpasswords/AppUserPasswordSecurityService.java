package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.exceptions.DailyNotEnoughSecurePasswordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import me.gosimple.nbvcxz.Nbvcxz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.stereotype.Service;

/** Deals with password security, such as complexity, whether it is compromised, ... */
@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserPasswordSecurityService {

  private final CompromisedPasswordChecker compromisedPasswordChecker;
  private final Nbvcxz nbvcxz;

  /**
   * Checks the security of the cleartext password passed in.
   *
   * @param cleartextPassword The cleartext password to check
   * @throws IllegalArgumentException When the password is blank
   * @throws CompromisedPasswordException When the password is compromised
   */
  public void check(final String cleartextPassword) {
    log.info("Checking a cleartext password for security");
    // The password should not be blank
    if (StringUtils.isBlank(cleartextPassword)) {
      log.info("The provided cleartext password is blank");
      throw new IllegalArgumentException("The chosen password is blank");
    }
    // The password should meet a standard set of security criteria
    if (isNotEnoughSecure(cleartextPassword)) {
      log.info("The provided cleartext password is not enough secure");
      throw new DailyNotEnoughSecurePasswordException("The chosen password is not enough secure");
    }
    // The password should not be compromised
    if (isCompromised(cleartextPassword)) {
      log.info("The provided cleartext password is compromised");
      throw new CompromisedPasswordException("The chosen password is compromised");
    }
    log.info("The provided cleartext password is secure");
  }

  private boolean isCompromised(final String cleartextPassword) {
    return compromisedPasswordChecker.check(cleartextPassword).isCompromised();
  }

  private boolean isNotEnoughSecure(final String cleartextPassword) {
    return !nbvcxz.estimate(cleartextPassword).isMinimumEntropyMet();
  }
}
