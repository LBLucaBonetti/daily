package it.lbsoftware.daily.config;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("WebSecurityConfiguration integration tests")
class WebSecurityConfigurationIntegrationTests extends DailyAbstractIntegrationTests {

  private static final String APP_USER = "appUser";

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should require secure when the Heroku header is present")
  void test1() throws Exception {
    // Given
    String herokuHeader = "X-Forwarded-Proto";

    // When
    String res =
        mockMvc
            .perform(get("/").with(loginOf(APP_USER)).header(herokuHeader, "value"))
            .andDo(print())
            .andReturn()
            .getResponse()
            .getRedirectedUrl();

    // Then
    assertNotNull(res);
    assertTrue(res.startsWith("https://"));
  }
}
