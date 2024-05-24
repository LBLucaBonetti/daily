package it.lbsoftware.daily.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

/**
 * This class creates a default {@code OidcUserService} bean to be injected into the custom {@code
 * AppUserOidcUserService} bean; the "default" prefix in the name is there to justify the customized
 * one that is configured in {@code WebSecurityConfiguration}.
 */
@Configuration
public class DefaultOidcUserServiceConfiguration {

  @Bean
  public OidcUserService oidcUserService() {
    return new OidcUserService();
  }
}
