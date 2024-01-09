package it.lbsoftware.daily;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(TestUtils.DailyDisplayNameGenerator.class)
class DailyApplicationTests extends DailyAbstractIntegrationTests {

  @Test
  @DisplayName("Should correctly load application context")
  void test1() {
    assertTrue(true);
  }
}
