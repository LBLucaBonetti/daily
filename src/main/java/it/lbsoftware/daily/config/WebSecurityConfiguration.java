package it.lbsoftware.daily.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // CSRF configuration
    http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
    // Authorization & authentication
    http.authorizeRequests().anyRequest().authenticated();
    // OAuth2 login
    http.oauth2Login()
        .successHandler(new SimpleUrlAuthenticationSuccessHandler("/"))
        // Overriding the login page avoids the automatically generated Spring Security login page
        // rendered to the /login endpoint which would show the Okta link to authenticate; the
        // /login, the /logout and the /login?logout endpoints will silently display index.html due
        // to the ErrorConfiguration controller handling the requests. Making a GET to
        // /oauth2/authorization/okta by entering the URL in the browser will simply do a new
        // "authorization dance"
        .loginPage("/oauth2/authorization/okta");
    http.exceptionHandling()
        .defaultAuthenticationEntryPointFor(
            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
            new AntPathRequestMatcher("/api/**"));
    return http.build();
  }
}
