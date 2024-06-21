package it.lbsoftware.daily.config;

import static it.lbsoftware.daily.config.Constants.CONTENT_SECURITY_POLICY;
import static it.lbsoftware.daily.config.Constants.PERMISSIONS_POLICY;

import it.lbsoftware.daily.appusers.AppUserOidcUserService;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/** Web security configurations. */
@Configuration
@AllArgsConstructor
public class WebSecurityConfiguration {

  private final CookieCsrfTokenRepository cookieCsrfTokenRepository;
  private final AppUserOidcUserService appUserOidcUserService;

  private String[] getAllowedPaths() {
    final List<String> allowedPaths = new ArrayList<>();
    allowedPaths.addAll(Constants.ALLOWED_STATIC_ASSETS);
    allowedPaths.addAll(Constants.ALLOWED_STATIC_TEMPLATES);
    // The login template and the login?error endpoint are already allowed

    return allowedPaths.toArray(new String[0]);
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
                // The referrer policy for the password reset requests should be no-referrer to
                // avoid referrer leakage
                .addHeaderWriter(
                    new DelegatingRequestMatcherHeaderWriter(
                        new AntPathRequestMatcher(Constants.PASSWORD_RESET_PATH),
                        new DailyReferrerPolicyHeaderWriter(
                            ReferrerPolicy.NO_REFERRER.getPolicy())))
                .permissionsPolicy(permissions -> permissions.policy(PERMISSIONS_POLICY)));
    // Authorization & authentication
    http.authorizeHttpRequests(
        authorizeHttpRequests ->
            authorizeHttpRequests
                .requestMatchers(getAllowedPaths())
                .permitAll()
                .anyRequest()
                .authenticated());
    // The /logout endpoint is automatically permitted but the /login?logout one is not
    http.logout(LogoutConfigurer::permitAll);
    http.exceptionHandling(
        exceptionHandling ->
            exceptionHandling.defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                new AntPathRequestMatcher("/api/**")));
    // Form login
    http.formLogin(
        formLogin ->
            formLogin
                .usernameParameter("email")
                .defaultSuccessUrl("/", true)
                .loginPage(Constants.LOGIN_PATH)
                .permitAll());
    // OAuth2 login
    http.oauth2Login(
        oauth2Login ->
            oauth2Login
                .defaultSuccessUrl("/", true)
                .loginPage(Constants.LOGIN_PATH)
                .permitAll()
                .userInfoEndpoint(
                    userInfoEndpoint -> userInfoEndpoint.oidcUserService(appUserOidcUserService)));
    return http.build();
  }

  @Bean
  AuthenticationEventPublisher authenticationEventPublisher(
      ApplicationEventPublisher applicationEventPublisher) {
    return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
  }

  @Bean
  CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }
}
