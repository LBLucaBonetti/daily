package it.lbsoftware.daily;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Daily application tests")
class DailyApplicationTests extends DailyAbstractTests {

  @Test
  @DisplayName("Context loads")
  void contextLoads() {}
}
