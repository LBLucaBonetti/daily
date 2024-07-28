package it.lbsoftware.daily.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("Referrer policy header writer integration tests")
class ReferrerPolicyHeaderWriterIntegrationTests extends DailyAbstractIntegrationTests {

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should set noreferrer referrer policy for get password reset path")
  void test1() throws Exception {
    // Given
    String passwordResetUrl = Constants.PASSWORD_RESET_PATH;

    // When and then
    mockMvc
        .perform(get(passwordResetUrl).queryParam("code", UUID.randomUUID().toString()))
        .andExpect(status().isOk())
        .andExpect(
            header()
                .string(
                    DailyReferrerPolicyHeaderWriter.REFERRER_POLICY_HEADER,
                    ReferrerPolicy.NO_REFERRER.getPolicy()));
  }

  @Test
  @DisplayName("Should set noreferrer referrer policy for post password reset path")
  void test2() throws Exception {
    // Given
    String passwordResetUrl = Constants.PASSWORD_RESET_PATH;

    // When and then
    mockMvc
        .perform(
            post(passwordResetUrl)
                .formField("password", "new-password")
                .formField("passwordConfirmation", "new-password")
                .formField("passwordResetCode", UUID.randomUUID().toString())
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(
            header()
                .string(
                    DailyReferrerPolicyHeaderWriter.REFERRER_POLICY_HEADER,
                    ReferrerPolicy.NO_REFERRER.getPolicy()));
  }

  @Test
  @DisplayName("Should set the default referrer policy for non-password-reset paths")
  void test3() throws Exception {
    // Given
    String anotherUrl = Constants.LOGIN_PATH;

    // When and then
    mockMvc
        .perform(get(anotherUrl))
        .andExpect(status().isOk())
        .andExpect(
            header()
                .string(
                    DailyReferrerPolicyHeaderWriter.REFERRER_POLICY_HEADER,
                    Constants.REFERRER_POLICY.getPolicy()));
  }
}
