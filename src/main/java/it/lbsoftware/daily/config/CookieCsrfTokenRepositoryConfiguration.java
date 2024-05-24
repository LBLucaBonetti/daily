package it.lbsoftware.daily.config;

import static it.lbsoftware.daily.config.Constants.COOKIE_CSRF_TOKEN_REPOSITORY_BEAN_NAME;
import static it.lbsoftware.daily.config.Constants.DAILY_COOKIE_CSRF_ENHANCED_SECURITY_KEY;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/** Configuration for cookies containing the CSRF protection token. */
@Configuration
@CommonsLog
public class CookieCsrfTokenRepositoryConfiguration {

  /**
   * Builds a cookie CSRF token repository with basic security.
   *
   * @return A cookie CSRF token repository with basic security
   */
  @Bean(name = COOKIE_CSRF_TOKEN_REPOSITORY_BEAN_NAME)
  @ConditionalOnProperty(
      name = DAILY_COOKIE_CSRF_ENHANCED_SECURITY_KEY,
      havingValue = "false",
      matchIfMissing = true)
  public CookieCsrfTokenRepository cookieCsrfTokenRepositoryWithBasicSecurity() {
    final CookieCsrfTokenRepository cookieCsrfTokenRepository =
        CookieCsrfTokenRepository.withHttpOnlyFalse();
    log.info("Building a CookieCsrfTokenRepository bean with HttpOnly set to false");
    return cookieCsrfTokenRepository;
  }

  /**
   * Builds a cookie CSRF token repository with enhanced security.
   *
   * @return A cookie CSRF token repository with enhanced security
   */
  @Bean(name = COOKIE_CSRF_TOKEN_REPOSITORY_BEAN_NAME)
  @ConditionalOnProperty(
      name = DAILY_COOKIE_CSRF_ENHANCED_SECURITY_KEY,
      havingValue = "true",
      matchIfMissing = false)
  public CookieCsrfTokenRepository cookieCsrfTokenRepositoryWithEnhancedSecurity() {
    final CookieCsrfTokenRepository cookieCsrfTokenRepository = new CookieCsrfTokenRepository();
    cookieCsrfTokenRepository.setCookieCustomizer(
        cookieCustomizer -> cookieCustomizer.httpOnly(false).secure(true).sameSite("Strict"));
    log.info(
        "Building a CookieCsrfTokenRepository bean with HttpOnly set to false, Secure set to true "
            + "and SameSite set to Strict");
    return cookieCsrfTokenRepository;
  }
}
