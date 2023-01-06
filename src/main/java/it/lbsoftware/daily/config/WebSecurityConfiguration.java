package it.lbsoftware.daily.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class WebSecurityConfiguration {

  private static final String LOGIN_PAGE = "/oauth2/authorization/google";
  private static final String LOGOUT_SUCCESS_URL = "https://www.google.com";
  private final CookieCsrfTokenRepository cookieCsrfTokenRepository;

  public WebSecurityConfiguration(final CookieCsrfTokenRepository cookieCsrfTokenRepository) {
    this.cookieCsrfTokenRepository = cookieCsrfTokenRepository;
  }

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
            headers.referrerPolicy(
                referrer -> referrer.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)));
    // Authorization & authentication
    http.authorizeHttpRequests(
        authorizeHttpRequests -> authorizeHttpRequests.anyRequest().authenticated());
    // OAuth2 login
    http.oauth2Login()
        .successHandler(new SimpleUrlAuthenticationSuccessHandler("/"))
        // Overriding the login page avoids the automatically generated Spring Security login page
        // rendered to the /login endpoint which would show the OAuth2 provider link to
        // authenticate; the /login, the /logout and the /login?logout endpoints will silently
        // display index.html due to the DailyErrorController handling the requests. Making a GET to
        // /oauth2/authorization/{oauth2-provider-name} by entering the URL in the browser will
        // simply do a new "authorization dance"
        .loginPage(LOGIN_PAGE)
        .and()
        .logout()
        .logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    http.exceptionHandling()
        .defaultAuthenticationEntryPointFor(
            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
            new AntPathRequestMatcher("/api/**"));
    return http.build();
  }
}
