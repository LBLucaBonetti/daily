package it.lbsoftware.daily.appusers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test", "okta"})
@ExtendWith(MockitoExtension.class)
class AppUserRegistrationFilterTests {

  @Mock private AppUserServiceImpl appUserService;
  @Mock private JwtAuthenticationToken jwtAuthenticationToken;
  @Mock private SecurityContext securityContext;
  @Mock private HttpServletRequest httpServletRequest;
  @Mock private HttpServletResponse httpServletResponse;
  @Mock private FilterChain filterChain;

  private AppUserRegistrationFilter appUserRegistrationFilter;

  @BeforeEach
  void setUp() {
    appUserRegistrationFilter = new AppUserRegistrationFilter(appUserService);
  }

  @AfterEach
  void tearDown() {}

  @Test
  void givenJwtAuthenticationToken_thenSetAppUserToToken() throws ServletException, IOException {
    given(securityContext.getAuthentication()).willReturn(jwtAuthenticationToken);
    SecurityContextHolder.setContext(securityContext);
    appUserRegistrationFilter.doFilterInternal(
        httpServletRequest, httpServletResponse, filterChain);
    verify(appUserService, times(1)).setAppUserToToken(jwtAuthenticationToken);
    verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
  }

  @Test
  void givenNoJwtAuthenticationToken_thenNoSetAppUserToToken()
      throws ServletException, IOException {
    given(securityContext.getAuthentication()).willReturn(null);
    SecurityContextHolder.setContext(securityContext);
    appUserRegistrationFilter.doFilterInternal(
        httpServletRequest, httpServletResponse, filterChain);
    verify(appUserService, times(0)).setAppUserToToken(jwtAuthenticationToken);
    verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
  }
}
