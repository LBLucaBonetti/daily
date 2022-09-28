package it.lbsoftware.daily.appusers;

import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public final class AppUserTestUtils {

  private static final String ID_TOKEN_VALUE = "id-token";
  public static final String UID_CLAIM = "sub";

  private AppUserTestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * App user generator
   *
   * @param idTokenClaims Key-value pairs for the id token claims; the sub claim is required
   * @return The created app user
   */
  public static OidcUser createAppUser(@NonNull final Map<String, Object> idTokenClaims) {
    return createAppUser(idTokenClaims, (String[]) null);
  }

  /**
   * App user generator
   *
   * @param idTokenClaims Key-value pairs for the id token claims; the sub claim is required
   * @param authorities Strings representing the authorities this user will have
   * @return The created app user
   */
  public static OidcUser createAppUser(
      @NonNull final Map<String, Object> idTokenClaims, final String... authorities) {
    return new DefaultOidcUser(
        Optional.ofNullable(authorities)
            .map(AuthorityUtils::createAuthorityList)
            .orElse(AuthorityUtils.NO_AUTHORITIES),
        OidcIdToken.withTokenValue(ID_TOKEN_VALUE).claims(c -> c.putAll(idTokenClaims)).build());
  }
}
