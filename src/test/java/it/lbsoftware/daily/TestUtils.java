package it.lbsoftware.daily;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.EMAIL_CLAIM;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.FULL_NAME_CLAIM;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.UID_CLAIM;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

import it.lbsoftware.daily.appusers.AppUserTestUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class TestUtils {

  // The order in which the table names are listed matters
  private static final String[] tableNames = {
    "note_tag",
    "tag",
    "note",
    "app_user_activation",
    "app_user_setting",
    "app_user_removal_information",
    "app_user_password_reset",
    "app_user"
  };

  private TestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * Deletes all data from the default, hardcoded table names.
   *
   * @param jdbcTemplate The jdbc handle instance to operate on
   * @return The number of deleted rows
   */
  public static int cleanDatabase(@NonNull final JdbcTemplate jdbcTemplate) {
    return JdbcTestUtils.deleteFromTables(jdbcTemplate, tableNames);
  }

  /**
   * Configures a mocked OpenID Connect login with an app user whose id token contains a sub claim
   * with the provided appUser as value.
   *
   * @param appUser UUID that will be used as the sub claim of the id token for the mocked app user
   * @return The configured RequestPostProcessor to be used to configure a MockMvc instance
   */
  public static RequestPostProcessor loginOf(@NonNull final UUID appUser) {
    return loginOf(appUser, null, null);
  }

  /**
   * Configures a mocked OpenID Connect login with an app user whose id token contains a sub claim
   * with the provided appUser as value, a name claim with the provided fullName and an e-mail claim
   * with the provided e-mail.
   *
   * @param appUser UUID that will be used as the sub claim of the id token for the mocked app user
   * @param fullName String that will be used as the name claim of the id token for the mocked app
   *     user
   * @param email String that will be used as the e-mail claim of the id token for the mocked app
   *     user
   * @return The configured RequestPostProcessor to be used to configure a MockMvc instance
   */
  public static RequestPostProcessor loginOf(
      @NonNull final UUID appUser, final String fullName, final String email) {
    Map<String, Object> idTokenClaims = new HashMap<>();
    idTokenClaims.put(UID_CLAIM, appUser);
    Optional.ofNullable(fullName).ifPresent(e -> idTokenClaims.put(FULL_NAME_CLAIM, e));
    Optional.ofNullable(email)
        .ifPresent(e -> idTokenClaims.put(EMAIL_CLAIM, StringUtils.toRootLowerCase(e)));

    return oidcLogin().oidcUser(AppUserTestUtils.createOauth2AppUser(idTokenClaims));
  }

  /**
   * Deletes all data from the defined caches.
   *
   * @param cacheManager The cache manager instance to operate on
   */
  public static void cleanCaches(@NonNull final CacheManager cacheManager) {
    cacheManager.getCacheNames().stream()
        .map(cacheManager::getCache)
        .filter(Objects::nonNull)
        .forEach(Cache::clear);
  }

  /**
   * A {@code DisplayNameGenerator} that displays the name of the class and its test type; if the
   * test class extends {@code DailyAbstractUnitTests}, it is considered a unit tests class; if the
   * test class extends {@code DailyAbstractIntegrationTests}, it is considered an integration tests
   * class. Note that if the test class name does not correspond to the class it refers to (and
   * tests methods of), the displayed name could be inappropriate. The default display name will be
   * composed of the test class name without the {@code SUFFIX} and a generic suffix.
   */
  public static class DailyDisplayNameGenerator extends DisplayNameGenerator.Standard {

    private static final String SUFFIX = "Tests";

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
      var classNameWithoutSuffix =
          super.generateDisplayNameForClass(testClass).replace(SUFFIX, StringUtils.EMPTY);
      if (DailyAbstractUnitTests.class.isAssignableFrom(testClass)) {
        return classNameWithoutSuffix + " unit tests";
      } else if (DailyAbstractIntegrationTests.class.isAssignableFrom(testClass)) {
        return classNameWithoutSuffix + " integration tests";
      } else {
        return classNameWithoutSuffix + " tests";
      }
    }
  }
}
