package it.lbsoftware.daily;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public non-sealed abstract class DailyAbstractIntegrationTests extends DailyAbstractTests {

  static {
    GenericContainer<?> redis =
        new GenericContainer<>(DockerImageName.parse("redis")).withExposedPorts(6379);
    redis.start();
    System.setProperty("spring.data.redis.host", redis.getHost());
    System.setProperty("spring.data.redis.port", redis.getMappedPort(6379).toString());
  }

  @Autowired protected WebApplicationContext webApplicationContext;
  protected MockMvc mockMvc;
  @Autowired private JdbcTemplate jdbcTemplate;

  @AfterEach
  void afterEach() {
    TestUtils.cleanDatabase(jdbcTemplate);
  }
}
