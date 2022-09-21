package it.lbsoftware.daily;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

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
  public static int cleanDatabase(JdbcTemplate jdbcTemplate) {
    return JdbcTestUtils.deleteFromTables(jdbcTemplate, tableNames);
  }
}
