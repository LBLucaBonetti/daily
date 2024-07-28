package it.lbsoftware.daily.config;

import me.gosimple.nbvcxz.Nbvcxz;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
