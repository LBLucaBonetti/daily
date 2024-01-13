package it.lbsoftware.daily;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public non-sealed abstract class DailyAbstractIntegrationTests extends DailyAbstractTests {

  private static final Integer MAILHOG_HTTP_PORT;
  private static final String MAILHOG_HOST_ADDRESS;

  static {
    // Initialize Redis
    GenericContainer<?> redis =
        new GenericContainer<>(DockerImageName.parse("redis")).withExposedPorts(6379);
    redis.start();
    System.setProperty("spring.data.redis.host", redis.getHost());
    System.setProperty("spring.data.redis.port", redis.getMappedPort(6379).toString());
    // Initialize MailHog
    GenericContainer<?> mailhog = new GenericContainer<>(DockerImageName.parse("mailhog/mailhog"))
        .withExposedPorts(1025, 8025);
    mailhog.start();
    System.setProperty("spring.mail.host",mailhog.getHost());
    System.setProperty("spring.mail.port",mailhog.getMappedPort(1025).toString());
    MAILHOG_HTTP_PORT = mailhog.getMappedPort(8025);
    MAILHOG_HOST_ADDRESS = mailhog.getHost();
  }

  @Autowired protected WebApplicationContext webApplicationContext;
  protected MockMvc mockMvc;
  @Autowired protected CacheManager cacheManager;
  @Autowired private JdbcTemplate jdbcTemplate;

  protected static void assertEmailMessageCount(final int expectedEmailMessageCount) {
    RestAssured.baseURI = "http://" + MAILHOG_HOST_ADDRESS;
    RestAssured.port = MAILHOG_HTTP_PORT;
    RestAssured.basePath = "/api/v2";
    var actualCount = new JsonPath(RestAssured.get("/messages").getBody().asString())
        .getInt("count");
    if(actualCount != expectedEmailMessageCount) {
      throw new AssertionFailedError(
          "The actual count of e-mail messages differs from the expected one; actual: " +
              actualCount +
              ", expected: " +
              expectedEmailMessageCount);
    }
  }

  private static void cleanEmails() {
    RestAssured.baseURI = "http://" + MAILHOG_HOST_ADDRESS;
    RestAssured.port = MAILHOG_HTTP_PORT;
    RestAssured.basePath = "/api/v1";
    RestAssured.delete("/messages");
  }

  @AfterEach
  void afterEach() {
    TestUtils.cleanDatabase(jdbcTemplate);
    TestUtils.cleanCaches(cacheManager);
    cleanEmails();
  }
}
