package it.lbsoftware.daily;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

public abstract class TestUtils {

  private static String[] tableNames = {"note_tag", "tag", "note", "app_user"};

  /**
   * Deletes all data from the default, hardcoded table names
   *
   * @param jdbcTemplate The jdbc handle instance to operate on
   * @return The number of rows the method deleted
   */
  public static int cleanDatabase(JdbcTemplate jdbcTemplate) {
    return JdbcTestUtils.deleteFromTables(jdbcTemplate, tableNames);
  }
}
