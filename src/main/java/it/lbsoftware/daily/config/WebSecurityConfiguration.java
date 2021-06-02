package it.lbsoftware.daily.config;

import com.okta.spring.boot.oauth.Okta;
import it.lbsoftware.daily.appusers.AppUserRegistrationFilter;
import it.lbsoftware.daily.appusers.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AppUserService appUserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Authorization & authentication
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        // Cross-origin resource sharing
        http
                .cors();
        // Cross-site request forgery (disable)
        http
                .csrf()
                .disable();
        // Stateless session policy
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // Custom filters
        http
                .addFilterAfter(new AppUserRegistrationFilter(appUserService), BearerTokenAuthenticationFilter.class);

        Okta.configureResourceServer401ResponseBody(http);
    }

}
