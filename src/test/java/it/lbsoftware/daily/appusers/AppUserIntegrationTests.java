package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("App user integration tests")
class AppUserIntegrationTests extends DailyAbstractIntegrationTests {

  private static final String BASE_URL = "/api/appusers";
  @Autowired private ObjectMapper objectMapper;
  @Autowired private AppUserRepository appUserRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should return unauthorized when read info and no auth")
  void test1() throws Exception {
    mockMvc.perform(get(BASE_URL + "/info").with(csrf())).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return empty fullName when fullName is null")
  void test2() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    InfoDto infoDto = new InfoDto(null, appUser.getEmail(), appUser.getAuthProvider());

    // When
    var res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/info")
                        .with(loginOf(appUser.getUuid(), infoDto.fullName(), infoDto.email())))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InfoDto.class);

    // Then
    assertEquals(StringUtils.EMPTY, res.fullName());
    assertEquals(infoDto.email(), res.email());
  }

  @Test
  @DisplayName("Should return empty email when email is null")
  void test3() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    InfoDto infoDto =
        new InfoDto(
            appUser.getFirstName() + " " + appUser.getLastName(), null, appUser.getAuthProvider());

    // When
    var res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/info")
                        .with(loginOf(appUser.getUuid(), infoDto.fullName(), infoDto.email())))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InfoDto.class);

    // Then
    assertEquals(infoDto.fullName(), res.fullName());
    assertEquals(StringUtils.EMPTY, res.email());
    assertEquals(infoDto.authProvider(), res.authProvider());
  }

  @Test
  @DisplayName("Should read info with OidcUser")
  void test4() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    InfoDto infoDto =
        new InfoDto(
            appUser.getFirstName() + " " + appUser.getLastName(),
            appUser.getEmail(),
            appUser.getAuthProvider());

    // When
    var res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/info")
                        .with(loginOf(appUser.getUuid(), infoDto.fullName(), infoDto.email())))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InfoDto.class);

    // Then
    assertEquals(infoDto.fullName(), res.fullName());
    assertEquals(infoDto.email(), res.email());
    assertEquals(infoDto.authProvider(), res.authProvider());
  }

  @Test
  @DisplayName("Should read info with AppUserDetails")
  void test5() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveDailyAppUser(appUserRepository, passwordEncoder);
    InfoDto infoDto =
        new InfoDto(
            appUser.getFirstName() + " " + appUser.getLastName(),
            appUser.getEmail(),
            appUser.getAuthProvider());

    // When
    var res =
        objectMapper.readValue(
            mockMvc
                .perform(get(BASE_URL + "/info").with(user(new AppUserDetails(appUser))))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InfoDto.class);

    // Then
    assertEquals(infoDto.fullName(), res.fullName());
    assertEquals(infoDto.email(), res.email());
    assertEquals(infoDto.authProvider(), res.authProvider());
  }
}
