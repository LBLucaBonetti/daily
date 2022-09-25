package it.lbsoftware.daily;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class DailyAbstractIntegrationTests extends DailyAbstractTests{

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  protected WebTestClient webTestClient;

  @AfterEach
  void afterEach() {
    TestUtils.cleanDatabase(jdbcTemplate);
  }

}
