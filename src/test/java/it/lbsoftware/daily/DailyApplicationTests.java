package it.lbsoftware.daily;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DailyApplication tests")
class DailyApplicationTests {

  @Test
  @DisplayName("Should not throw when the application starts")
  void test1() {
    DailyApplication.main(
        new String[] {
          "--spring.profiles.active=test,oauth2",
          "--daily.cookie.csrf.secure=true",
          "--daily.cookie.csrf.same-site=STRICT"
        });
    assertTrue(true);
  }
}