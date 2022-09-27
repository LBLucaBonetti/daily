package it.lbsoftware.daily;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.UID_CLAIM;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

import java.util.Map;
import lombok.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class TestUtils {

  private TestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  private static final String[] tableNames = {"note_tag", "tag", "note"};

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
   * Configures a mocked OpenID Connect login with an app user whose id token contains a sub claim with the provided appUser as value
   *
   * @param appUser String that will be used as the sub claim of the id token for the mocked app user
   * @return The configured RequestPostProcessor to be used to configure a MockMvc instance
   */
  public static RequestPostProcessor loginOf(
      @NonNull final String appUser
  ) {
    return oidcLogin()
        .oidcUser(
            createAppUser(
                Map.of(UID_CLAIM, appUser)
            )
        );
  }
}
