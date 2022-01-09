package it.lbsoftware.daily.appusers;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class AppUserRegistrationFilter extends OncePerRequestFilter {

  private final AppUserService appUserService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      FilterChain filterChain)
      throws ServletException, IOException {
    JwtAuthenticationToken jwtAuthenticationToken =
        (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    if (jwtAuthenticationToken != null) {
      appUserService.setAppUserToToken(jwtAuthenticationToken);
    }

    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }
}
