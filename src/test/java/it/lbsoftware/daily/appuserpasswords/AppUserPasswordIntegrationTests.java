package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.config.Constants.LOGIN_VIEW;
import static it.lbsoftware.daily.config.Constants.PASSWORD_RESET_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserTestUtils;
import it.lbsoftware.daily.config.Constants;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("App user password integration tests")
class AppUserPasswordIntegrationTests extends DailyAbstractIntegrationTests {

  @Autowired private AppUserRepository appUserRepository;
  @Autowired private AppUserPasswordResetRepository appUserPasswordResetRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should not send a notification to reset the password to a non-daily AppUser")
  void test1() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    assertEmailMessageCount(0);
    assertEquals(0, appUserPasswordResetRepository.count());

    // When
    mockMvc
        .perform(
            post(Constants.SEND_PASSWORD_RESET_NOTIFICATION_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", appUser.getEmail())
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(LOGIN_VIEW));

    // Then
    assertEmailMessageCount(0);
    assertEquals(0, appUserPasswordResetRepository.count());
  }

  @Test
  @DisplayName("Should send a notification to reset the password to a daily AppUser")
  void test2() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveDaily2AppUser(appUserRepository);
    assertEmailMessageCount(0);
    assertEquals(0, appUserPasswordResetRepository.count());

    // When
    mockMvc
        .perform(
            post(Constants.SEND_PASSWORD_RESET_NOTIFICATION_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", appUser.getEmail())
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(LOGIN_VIEW));

    // Then
    assertEmailMessageCount(1);
    assertEquals(1, appUserPasswordResetRepository.count());
  }

  @Test
  @DisplayName("Should reset daily AppUser password")
  void test3() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveDaily2AppUser(appUserRepository);
    assertEmailMessageCount(0);
    var passwordResetCode = UUID.randomUUID();
    appUserPasswordResetRepository.save(
        AppUserPasswordReset.builder()
            .appUser(appUser)
            .expiredAt(
                LocalDateTime.now()
                    .plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES))
            .usedAt(null)
            .passwordResetCode(passwordResetCode)
            .build());
    var newPassword = "91b17128-f2bd-456a-ae0a-01e7d51e8df4";

    // When
    mockMvc
        .perform(
            post(Constants.PASSWORD_RESET_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("password", newPassword)
                .param("passwordConfirmation", newPassword)
                .param("passwordResetCode", passwordResetCode.toString())
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(LOGIN_VIEW));

    // Then
    assertEmailMessageCount(1);
    var savedAppUser = appUserRepository.findByEmailIgnoreCase(appUser.getEmail()).orElseThrow();
    var savedAppUserPasswordReset =
        appUserPasswordResetRepository.findByAppUser(savedAppUser).orElseThrow();
    assertTrue(passwordEncoder.matches(newPassword, savedAppUser.getPassword()));
    assertNotNull(savedAppUserPasswordReset.getUsedAt());
  }

  @Test
  @DisplayName("Should not reset non-daily AppUser password")
  void test4() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    assertEmailMessageCount(0);
    var passwordResetCode = UUID.randomUUID();
    var newPassword = "91b17128-f2bd-456a-ae0a-01e7d51e8df4";

    // When
    mockMvc
        .perform(
            post(Constants.PASSWORD_RESET_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("password", newPassword)
                .param("passwordConfirmation", newPassword)
                .param("passwordResetCode", passwordResetCode.toString())
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(PASSWORD_RESET_VIEW));

    // Then
    assertEmailMessageCount(0);
    var savedAppUser = appUserRepository.findByEmailIgnoreCase(appUser.getEmail()).orElseThrow();
    assertFalse(passwordEncoder.matches(newPassword, savedAppUser.getPassword()));
  }
}
