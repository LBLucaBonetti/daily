package it.lbsoftware.daily.config;

import static it.lbsoftware.daily.config.Constants.CONTENT_SECURITY_POLICY;
import static it.lbsoftware.daily.config.Constants.PERMISSIONS_POLICY;

import it.lbsoftware.daily.appusers.AppUserDetailsService;
import it.lbsoftware.daily.appusers.AppUserOidcUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@AllArgsConstructor
public class WebSecurityConfiguration {

  private final CookieCsrfTokenRepository cookieCsrfTokenRepository;
  private final AppUserOidcUserService appUserOidcUserService;
  private final AppUserDetailsService appUserDetailsService;
  private final PasswordEncoder passwordEncoder;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // CSRF configuration
    CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
    // Set the name of the attribute the CsrfToken will be populated on
    requestHandler.setCsrfRequestAttributeName(null);
    http.csrf(
        csrf ->
            csrf.csrfTokenRepository(cookieCsrfTokenRepository)
                .csrfTokenRequestHandler(requestHandler));
    // Security headers
    http.headers(
        headers ->
            headers
                .contentSecurityPolicy(csp -> csp.policyDirectives(CONTENT_SECURITY_POLICY))
                .referrerPolicy(
                    referrer -> referrer.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .permissionsPolicy(permissions -> permissions.policy(PERMISSIONS_POLICY)));
    // Authorization & authentication
    http.authorizeHttpRequests(
        authorizeHttpRequests ->
            authorizeHttpRequests
                .requestMatchers(Constants.SIGNUP_PATH)
                .permitAll()
                .anyRequest()
                .authenticated());
    // Form login
    http.formLogin()
        .usernameParameter("email")
        .defaultSuccessUrl("/", true)
        .loginPage(Constants.LOGIN_PATH)
        .permitAll();
    // OAuth2 login
    http.oauth2Login()
        .defaultSuccessUrl("/", true)
        .loginPage(Constants.LOGIN_PATH)
        .permitAll()
        .userInfoEndpoint()
        .oidcUserService(appUserOidcUserService);
    http.exceptionHandling()
        .defaultAuthenticationEntryPointFor(
            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
            new AntPathRequestMatcher("/api/**"));
    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    var authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setPasswordEncoder(passwordEncoder);
    authenticationProvider.setUserDetailsService(appUserDetailsService);
    return authenticationProvider;
  }
}
