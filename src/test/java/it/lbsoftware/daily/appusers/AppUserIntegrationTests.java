package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("AppUser integration tests")
class AppUserIntegrationTests extends DailyAbstractIntegrationTests {
  private static final String BASE_URL = "/api/appusers";
  private static final String APP_USER = "appUser";
  private static final String FULL_NAME = "name surname";
  private static final String EMAIL = "email@email.com";
  @Autowired private ObjectMapper objectMapper;

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
    InfoDto infoDto = new InfoDto(null, EMAIL);

    // When
    var res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/info")
                        .with(loginOf(APP_USER, infoDto.fullName(), infoDto.email())))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InfoDto.class);

    // Then
    assertEquals("", res.fullName());
    assertEquals(infoDto.email(), res.email());
  }

  @Test
  @DisplayName("Should return empty email when email is null")
  void test3() throws Exception {
    // Given
    InfoDto infoDto = new InfoDto(FULL_NAME, null);

    // When
    var res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/info")
                        .with(loginOf(APP_USER, infoDto.fullName(), infoDto.email())))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InfoDto.class);

    // Then
    assertEquals(infoDto.fullName(), res.fullName());
    assertEquals("", res.email());
  }

  @Test
  @DisplayName("Should read info")
  void test4() throws Exception {
    // Given
    InfoDto infoDto = new InfoDto(FULL_NAME, EMAIL);

    // When
    var res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/info")
                        .with(loginOf(APP_USER, infoDto.fullName(), infoDto.email())))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InfoDto.class);

    // Then
    assertEquals(infoDto.fullName(), res.fullName());
    assertEquals(infoDto.email(), res.email());
  }
}
