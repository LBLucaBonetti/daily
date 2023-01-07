package it.lbsoftware.daily.config;

import static it.lbsoftware.daily.config.Constants.CSRF_TOKEN_NAME;
import static it.lbsoftware.daily.config.Constants.DAILY_COOKIE_CSRF_SAME_SITE_KEY;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@CommonsLog
public class SameSiteConfiguration {

  @Bean
  @ConditionalOnProperty(name = DAILY_COOKIE_CSRF_SAME_SITE_KEY, havingValue = "STRICT")
  public CookieSameSiteSupplier applicationCookieSameSiteSupplier() {
    final CookieSameSiteSupplier cookieSameSiteSupplier =
        CookieSameSiteSupplier.ofStrict().whenHasName(CSRF_TOKEN_NAME);
    log.info(
        "Building a CookieSameSiteSupplier bean to apply STRICT to cookies named "
            + CSRF_TOKEN_NAME);
    return cookieSameSiteSupplier;
  }
}
