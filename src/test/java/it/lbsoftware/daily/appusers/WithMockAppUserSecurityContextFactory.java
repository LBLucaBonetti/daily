package it.lbsoftware.daily.appusers;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockAppUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockAppUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockAppUser withMockAppUser) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    if ("".equals(withMockAppUser.email()) || "".equals(withMockAppUser.uid())) {
      // If the email or the uid are blank, then provide no authentication to the context
      return context;
    }
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", withMockAppUser.email())
            .claim("uid", withMockAppUser.uid())
            .build();
    Collection<GrantedAuthority> authorities =
        AuthorityUtils.createAuthorityList("SCOPE_read", "SCOPE_write");
    JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);
    if (withMockAppUser.setAppUserAsDetails()) {
      // Populate details with AppUser data and set token details
      AppUser appUser =
          AppUser.builder().uid(withMockAppUser.uid()).email(withMockAppUser.email()).build();
      token.setDetails(appUser);
    }
    context.setAuthentication(token);

    return context;
  }
}
