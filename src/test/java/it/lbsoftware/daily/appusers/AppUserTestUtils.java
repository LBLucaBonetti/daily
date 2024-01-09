package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.bases.BaseEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
   * App user generator; the created instance is not persisted
   *
   * @param idTokenClaims Key-value pairs for the id token claims; the sub claim is required
   * @return The created app user
   */
  public static OidcUser createOauth2AppUser(@NonNull final Map<String, Object> idTokenClaims) {
    return createOauth2AppUser(idTokenClaims, (String[]) null);
  }

  /**
   * App user generator; the created instance is not persisted
   *
   * @param idTokenClaims Key-value pairs for the id token claims; the sub claim is required
   * @param authorities Strings representing the authorities this user will have
   * @return The created app user
   */
  public static OidcUser createOauth2AppUser(
      @NonNull final Map<String, Object> idTokenClaims, final String... authorities) {
    return new DefaultOidcUser(
        Optional.ofNullable(authorities)
            .map(AuthorityUtils::createAuthorityList)
            .orElse(AuthorityUtils.NO_AUTHORITIES),
        OidcIdToken.withTokenValue(ID_TOKEN_VALUE).claims(c -> c.putAll(idTokenClaims)).build());
  }

  /**
   * App user generator with default values; the created instance is persisted with the provided
   * repository
   *
   * @param appUserRepository Repository used to persist the created instance
   * @return The saved entity
   */
  public static AppUser saveOauth2AppUser(@NonNull final AppUserRepository appUserRepository) {
    return appUserRepository.save(
        AppUser.builder()
            .enabled(true)
            .authProviderId(APP_USER_AUTH_PROVIDER_ID)
            .authProvider(AuthProvider.GOOGLE)
            .firstName(APP_USER_FIRSTNAME)
            .lastName(APP_USER_LASTNAME)
            .email(APP_USER_EMAIL)
            .build());
  }

  /**
   * App user generator with different default values; the created instance is persisted with the
   * provided repository
   *
   * @param appUserRepository Repository used to persist the created instance
   * @return The saved entity
   */
  public static AppUser saveOauth2OtherAppUser(@NonNull final AppUserRepository appUserRepository) {
    return appUserRepository.save(
        AppUser.builder()
            .enabled(true)
            .authProviderId(OTHER_APP_USER_AUTH_PROVIDER_ID)
            .authProvider(AuthProvider.GOOGLE)
            .firstName(OTHER_APP_USER_FIRSTNAME)
            .lastName(OTHER_APP_USER_LASTNAME)
            .email(OTHER_APP_USER_EMAIL)
            .build());
  }

  /**
   * AppUser generator; the created instance is not persisted
   *
   * @param uuid The entity UUID
   * @param email The e-mail
   * @return The created AppUser
   */
  public static AppUser createAppUser(@NonNull final UUID uuid, @NonNull final String email) {
    return createAppUser(uuid, email, null, null);
  }

  /**
   * AppUser generator; the created instance is not persisted
   *
   * @param uuid The entity UUID
   * @param email The e-mail
   * @param authProvider The auth provider
   * @param authProviderId The auth provider id
   * @return The created AppUser
   */
  public static AppUser createAppUser(
      @NonNull final UUID uuid,
      @NonNull final String email,
      final AuthProvider authProvider,
      final String authProviderId) {
    var appUser = new AppUser();
    Method setUuidMethod;
    try {
      setUuidMethod = BaseEntity.class.getDeclaredMethod("setUuid", UUID.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    setUuidMethod.setAccessible(true);
    try {
      setUuidMethod.invoke(appUser, uuid);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
    setUuidMethod.setAccessible(false);
    appUser.setEmail(email);
    appUser.setAuthProvider(authProvider);
    appUser.setAuthProviderId(authProviderId);
    return appUser;
  }
}
