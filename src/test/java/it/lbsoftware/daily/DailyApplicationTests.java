package it.lbsoftware.daily;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test", "okta"})
@SpringBootTest
class DailyApplicationTests {

  @Test
  void contextLoads() {}
}
