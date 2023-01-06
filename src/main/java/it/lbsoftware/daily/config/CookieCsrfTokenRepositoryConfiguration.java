package it.lbsoftware.daily.config;

import static it.lbsoftware.daily.config.Constants.COOKIE_CSRF_TOKEN_REPOSITORY_BEAN_NAME;
import static it.lbsoftware.daily.config.Constants.DAILY_COOKIE_CSRF_SECURE_KEY;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@CommonsLog
public class CookieCsrfTokenRepositoryConfiguration {

  @Bean(name = COOKIE_CSRF_TOKEN_REPOSITORY_BEAN_NAME)
  @ConditionalOnProperty(
      name = DAILY_COOKIE_CSRF_SECURE_KEY,
      havingValue = "false",
      matchIfMissing = true)
  public CookieCsrfTokenRepository cookieCsrfTokenRepositoryWithHttpOnlyFalse() {
    final CookieCsrfTokenRepository cookieCsrfTokenRepository =
        CookieCsrfTokenRepository.withHttpOnlyFalse();
    log.info("Built a CookieCsrfTokenRepository bean with HttpOnly set to false");
    return cookieCsrfTokenRepository;
  }

  @Bean(name = COOKIE_CSRF_TOKEN_REPOSITORY_BEAN_NAME)
  @ConditionalOnProperty(name = DAILY_COOKIE_CSRF_SECURE_KEY, havingValue = "true")
  public CookieCsrfTokenRepository cookieCsrfTokenRepositoryWithHttpOnlyFalseAndSecure() {
    final CookieCsrfTokenRepository cookieCsrfTokenRepository =
        CookieCsrfTokenRepository.withHttpOnlyFalse();
    cookieCsrfTokenRepository.setSecure(true);
    log.info(
        "Built a CookieCsrfTokenRepository bean with HttpOnly set to false and Secure set to true");
    return cookieCsrfTokenRepository;
  }
}
