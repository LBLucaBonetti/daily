package it.lbsoftware.daily.config;

import me.gosimple.nbvcxz.Nbvcxz;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

/**
 * Builds required configuration beans for {@link
 * it.lbsoftware.daily.appuserpasswords.AppUserPasswordSecurityService}.
 */
@Configuration
public class PasswordStrengthConfiguration {

  @Bean
  public Nbvcxz nbvcxz() {
    return new Nbvcxz();
  }

  @Bean
  public CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }
}
