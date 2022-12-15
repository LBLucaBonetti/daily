package it.lbsoftware.daily;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DailyApplication integration tests")
class DailyApplicationIntegrationTests extends DailyAbstractIntegrationTests {

  @Test
  @DisplayName("Should not throw when the application starts")
  void test1() {
    DailyApplication.main(new String[] {"--spring.profiles.active=test,oauth2"});
    assertTrue(true);
  }
}
