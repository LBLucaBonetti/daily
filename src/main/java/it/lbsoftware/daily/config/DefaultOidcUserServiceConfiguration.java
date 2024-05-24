package it.lbsoftware.daily.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

/**
 * Creates a default {@link OidcUserService} bean to be injected into the custom {@link
 * it.lbsoftware.daily.appusers.AppUserOidcUserService} bean; the "default" prefix in the name is
 * there to justify the customized one that is configured in {@link WebSecurityConfiguration}.
 */
@Configuration
public class DefaultOidcUserServiceConfiguration {

  @Bean
  public OidcUserService oidcUserService() {
    return new OidcUserService();
  }
}
