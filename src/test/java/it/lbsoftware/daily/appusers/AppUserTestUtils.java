package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public final class AppUserTestUtils {

  public static final String UID_CLAIM = "sub";
  public static final String FULL_NAME_CLAIM = "name";
  public static final String EMAIL_CLAIM = "email";
  public static final String APP_USER_AUTH_PROVIDER_ID = "appUserAuthProviderId";
  public static final String APP_USER_FIRSTNAME = "FirstName";
  public static final String APP_USER_LASTNAME = "LastName";
  public static final String APP_USER_FULLNAME = APP_USER_FIRSTNAME + " " + APP_USER_LASTNAME;
  public static final String APP_USER_EMAIL = "appUser@gmail.com";
  public static final String OTHER_APP_USER_AUTH_PROVIDER_ID = "otherAppUserAuthProviderId";
  public static final String OTHER_APP_USER_FIRSTNAME = "OtherFirstName";
  public static final String OTHER_APP_USER_LASTNAME = "OtherLastName";
  public static final String OTHER_APP_USER_FULLNAME =
      OTHER_APP_USER_FIRSTNAME + " " + OTHER_APP_USER_LASTNAME;
  public static final String OTHER_APP_USER_EMAIL = "otherAppUser@gmail.com";
  private static final String ID_TOKEN_VALUE = "id-token";

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

  public static UUID createOauth2AppUser(@NonNull final AppUserRepository appUserRepository) {
    return appUserRepository
        .save(
            AppUser.builder()
                .enabled(true)
                .authProviderId(APP_USER_AUTH_PROVIDER_ID)
                .authProvider(AuthProvider.GOOGLE)
                .firstName(APP_USER_FIRSTNAME)
                .lastName(APP_USER_LASTNAME)
                .email(APP_USER_EMAIL)
                .build())
        .getUuid();
  }

  public static UUID createOauth2OtherAppUser(@NonNull final AppUserRepository appUserRepository) {
    return appUserRepository
        .save(
            AppUser.builder()
                .enabled(true)
                .authProviderId(OTHER_APP_USER_AUTH_PROVIDER_ID)
                .authProvider(AuthProvider.GOOGLE)
                .firstName(OTHER_APP_USER_FIRSTNAME)
                .lastName(OTHER_APP_USER_LASTNAME)
                .email(OTHER_APP_USER_EMAIL)
                .build())
        .getUuid();
  }
}
