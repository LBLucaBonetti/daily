package it.lbsoftware.daily.appusers;

import java.util.Optional;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface AppUserService {

  /**
   * Creates a new app user if it does not exist, updates its information otherwise; details are
   * stored as Authentication details
   *
   * @param jwtAuthenticationToken Token that provides app user details and to set app user details
   *     to
   */
  void setAppUserToToken(JwtAuthenticationToken jwtAuthenticationToken);

  /**
   * Gets the request app user from the token
   *
   * @return Found app user or empty value
   */
  Optional<AppUser> getAppUserFromToken();
}
