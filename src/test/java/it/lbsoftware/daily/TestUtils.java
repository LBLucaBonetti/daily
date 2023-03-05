package it.lbsoftware.daily;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.EMAIL_CLAIM;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.FULL_NAME_CLAIM;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.UID_CLAIM;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class TestUtils {

  // The order in which the table names are listed matters
  private static final String[] tableNames = {"note_tag", "tag", "note"};

  private TestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * Deletes all data from the default, hardcoded table names
   *
   * @param jdbcTemplate The jdbc handle instance to operate on
   * @return The number of deleted rows
   */
  public static int cleanDatabase(@NonNull final JdbcTemplate jdbcTemplate) {
    return JdbcTestUtils.deleteFromTables(jdbcTemplate, tableNames);
  }

  /**
   * Configures a mocked OpenID Connect login with an app user whose id token contains a sub claim
   * with the provided appUser as value
   *
   * @param appUser String that will be used as the sub claim of the id token for the mocked app
   *     user
   * @return The configured RequestPostProcessor to be used to configure a MockMvc instance
   */
  public static RequestPostProcessor loginOf(@NonNull final String appUser) {
    return loginOf(appUser, null, null);
  }

  /**
   * Configures a mocked OpenID Connect login with an app user whose id token contains a sub claim
   * with the provided appUser as value, a name claim with the provided fullName and an email claim
   * with the provided email
   *
   * @param appUser String that will be used as the sub claim of the id token for the mocked app
   *     user
   * @param fullName String that will be used as the name claim of the id token for the mocked app
   *     user
   * @param email String that will be used as the email claim of the id token for the mocked app
   *     user
   * @return The configured RequestPostProcessor to be used to configure a MockMvc instance
   */
  public static RequestPostProcessor loginOf(
      @NonNull final String appUser, final String fullName, final String email) {
    Map<String, Object> idTokenClaims = new HashMap<>();
    idTokenClaims.put(UID_CLAIM, appUser);
    Optional.ofNullable(fullName).ifPresent(e -> idTokenClaims.put(FULL_NAME_CLAIM, e));
    Optional.ofNullable(email).ifPresent(e -> idTokenClaims.put(EMAIL_CLAIM, e));

    return oidcLogin().oidcUser(createAppUser(idTokenClaims));
  }

  /**
   * Deletes all data from the defined caches
   *
   * @param cacheManager The cache manager instance to operate on
   */
  public static void cleanCaches(@NonNull final CacheManager cacheManager) {
    cacheManager.getCacheNames().stream()
        .map(cacheManager::getCache)
        .filter(Objects::nonNull)
        .forEach(Cache::clear);
  }
}
