package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_CLEARTEXT_PASSWORD;
import static it.lbsoftware.daily.config.Constants.LOGIN_VIEW;
import static it.lbsoftware.daily.config.Constants.PASSWORD_RESET_VIEW;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import it.lbsoftware.daily.appusers.AppUserDetails;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserTestUtils;
import it.lbsoftware.daily.config.Constants;
import java.time.LocalDateTime;
import java.util.Map;
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

  private static final String NEW_PASSWORD = "91b17128-f2bd-456a-ae0a-01e7d51e8df4";
  @Autowired private AppUserRepository appUserRepository;
  @Autowired private AppUserPasswordResetRepository appUserPasswordResetRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should not send a notification to reset the password to a non-daily AppUser")
  void test1() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
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
    var appUser = AppUserTestUtils.saveDailyAppUser(appUserRepository, passwordEncoder);
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
    var appUser = AppUserTestUtils.saveDailyAppUser(appUserRepository, passwordEncoder);
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

    // When
    mockMvc
        .perform(
            post(Constants.PASSWORD_RESET_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("password", NEW_PASSWORD)
                .param("passwordConfirmation", NEW_PASSWORD)
                .param("passwordResetCode", passwordResetCode.toString())
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(LOGIN_VIEW));

    // Then
    assertEmailMessageCount(1);
    var savedAppUser = appUserRepository.findByEmailIgnoreCase(appUser.getEmail()).orElseThrow();
    var savedAppUserPasswordReset =
        appUserPasswordResetRepository.findByAppUser(savedAppUser).orElseThrow();
    assertTrue(passwordEncoder.matches(NEW_PASSWORD, savedAppUser.getPassword()));
    assertNotNull(savedAppUserPasswordReset.getUsedAt());
  }

  @Test
  @DisplayName("Should not reset non-daily AppUser password")
  void test4() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    assertEmailMessageCount(0);
    var passwordResetCode = UUID.randomUUID();
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, appUser.getPassword()));

    // When
    mockMvc
        .perform(
            post(Constants.PASSWORD_RESET_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("password", NEW_PASSWORD)
                .param("passwordConfirmation", NEW_PASSWORD)
                .param("passwordResetCode", passwordResetCode.toString())
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(PASSWORD_RESET_VIEW));

    // Then
    assertEmailMessageCount(0);
    var savedAppUser = appUserRepository.findByEmailIgnoreCase(appUser.getEmail()).orElseThrow();
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, savedAppUser.getPassword()));
  }

  @Test
  @DisplayName("Should change daily AppUser password")
  void test5() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveDailyAppUser(appUserRepository, passwordEncoder);
    assertEmailMessageCount(0);
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, appUser.getPassword()));
    var passwordChangeDto =
        new PasswordChangeDto(APP_USER_CLEARTEXT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

    // When
    mockMvc
        .perform(
            put(Constants.APP_USER_PATH + "/passwords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordChangeDto))
                .with(user(new AppUserDetails(appUser)))
                .with(csrf()))
        .andExpect(status().isNoContent());

    // Then
    assertEmailMessageCount(1);
    var savedAppUser = appUserRepository.findByEmailIgnoreCase(appUser.getEmail()).orElseThrow();
    assertTrue(passwordEncoder.matches(NEW_PASSWORD, savedAppUser.getPassword()));
  }

  @Test
  @DisplayName("Should not change daily AppUser password when the old one is wrong")
  void test6() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveDailyAppUser(appUserRepository, passwordEncoder);
    assertEmailMessageCount(0);
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, appUser.getPassword()));
    var passwordChangeDto =
        new PasswordChangeDto("wrong" + APP_USER_CLEARTEXT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

    // When
    Map<String, String> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    put(Constants.APP_USER_PATH + "/passwords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto))
                        .with(user(new AppUserDetails(appUser)))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    assertEmailMessageCount(0);
    var savedAppUser = appUserRepository.findByEmailIgnoreCase(appUser.getEmail()).orElseThrow();
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, savedAppUser.getPassword()));
    assertEquals(Constants.ERROR_PASSWORD_CHANGE_GENERIC, res.get(Constants.ERROR_KEY));
  }

  @Test
  @DisplayName("Should not change daily AppUser password when the AppUser is an oauth2 one")
  void test7() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveDailyAppUser(appUserRepository, passwordEncoder);
    var oauth2AppUser = AppUserTestUtils.saveOauth2OtherAppUser(appUserRepository, passwordEncoder);
    assertEmailMessageCount(0);
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, appUser.getPassword()));
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, oauth2AppUser.getPassword()));
    var passwordChangeDto =
        new PasswordChangeDto("wrong" + APP_USER_CLEARTEXT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

    // When
    Map<String, String> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    put(Constants.APP_USER_PATH + "/passwords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto))
                        .with(loginOf(oauth2AppUser.getUuid(), null, oauth2AppUser.getEmail()))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    assertEmailMessageCount(0);
    var savedAppUser = appUserRepository.findByEmailIgnoreCase(appUser.getEmail()).orElseThrow();
    var savedOauth2AppUser =
        appUserRepository.findByEmailIgnoreCase(oauth2AppUser.getEmail()).orElseThrow();
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, savedAppUser.getPassword()));
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, savedOauth2AppUser.getPassword()));
    assertEquals(Constants.ERROR_PASSWORD_CHANGE_GENERIC, res.get(Constants.ERROR_KEY));
  }

  @Test
  @DisplayName("Should not change daily AppUser password when the new ones do not match")
  void test8() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveDailyAppUser(appUserRepository, passwordEncoder);
    assertEmailMessageCount(0);
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, appUser.getPassword()));
    var passwordChangeDto =
        new PasswordChangeDto(
            APP_USER_CLEARTEXT_PASSWORD, NEW_PASSWORD, "wrongRepeated" + NEW_PASSWORD);

    // When
    Map<String, String> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    put(Constants.APP_USER_PATH + "/passwords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto))
                        .with(user(new AppUserDetails(appUser)))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    assertEmailMessageCount(0);
    var savedAppUser = appUserRepository.findByEmailIgnoreCase(appUser.getEmail()).orElseThrow();
    assertFalse(passwordEncoder.matches(NEW_PASSWORD, savedAppUser.getPassword()));
    assertFalse(
        passwordEncoder.matches("wrongRepeated" + NEW_PASSWORD, savedAppUser.getPassword()));
    assertEquals(Constants.ERROR_PASSWORD_CHANGE_MISMATCH, res.get(Constants.ERROR_KEY));
  }

  @Test
  @DisplayName("Should not change daily AppUser password when the new one is not secure")
  void test9() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveDailyAppUser(appUserRepository, passwordEncoder);
    assertEmailMessageCount(0);
    var newInsecurePassword = "1Pazzword!";
    var passwordChangeDto =
        new PasswordChangeDto(
            APP_USER_CLEARTEXT_PASSWORD, newInsecurePassword, newInsecurePassword);

    // When
    Map<String, String> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    put(Constants.APP_USER_PATH + "/passwords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto))
                        .with(user(new AppUserDetails(appUser)))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    assertEmailMessageCount(0);
    var savedAppUser = appUserRepository.findByEmailIgnoreCase(appUser.getEmail()).orElseThrow();
    assertFalse(passwordEncoder.matches(newInsecurePassword, savedAppUser.getPassword()));
    assertThat(
        res.get(Constants.ERROR_KEY),
        anyOf(
            is(Constants.ERROR_PASSWORD_CHANGE_COMPROMISED),
            is(Constants.ERROR_PASSWORD_CHANGE_GENERIC)));
  }

  @Test
  @DisplayName("Should not change daily AppUser password when the new one is not enough secure")
  void test10() throws Exception {
    // Given
    var appUser = AppUserTestUtils.saveDailyAppUser(appUserRepository, passwordEncoder);
    assertEmailMessageCount(0);
    var newNotEnoughSecurePassword = "APasswordNotComplexEnough";
    var passwordChangeDto =
        new PasswordChangeDto(
            APP_USER_CLEARTEXT_PASSWORD, newNotEnoughSecurePassword, newNotEnoughSecurePassword);

    // When
    Map<String, String> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    put(Constants.APP_USER_PATH + "/passwords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeDto))
                        .with(user(new AppUserDetails(appUser)))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    assertEmailMessageCount(0);
    var savedAppUser = appUserRepository.findByEmailIgnoreCase(appUser.getEmail()).orElseThrow();
    assertFalse(passwordEncoder.matches(newNotEnoughSecurePassword, savedAppUser.getPassword()));
    assertEquals(Constants.ERROR_PASSWORD_CHANGE_GENERIC, res.get(Constants.ERROR_KEY));
  }
}
