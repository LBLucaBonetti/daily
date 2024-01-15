package it.lbsoftware.daily.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    args = {
      "--" + Constants.DAILY_ASYNC_ENABLED + "=true",
    })
class AsyncConfigurationTests extends DailyAbstractIntegrationTests {

  @Test
  @DisplayName("Should build context with async processing enabled")
  void test1() {
    assertTrue(true);
  }
}
