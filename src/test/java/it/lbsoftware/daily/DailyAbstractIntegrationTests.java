package it.lbsoftware.daily;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class DailyAbstractIntegrationTests extends DailyAbstractTests {

  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired protected WebApplicationContext webApplicationContext;
  protected MockMvc mockMvc;

  @AfterEach
  void afterEach() {
    TestUtils.cleanDatabase(jdbcTemplate);
  }
}
