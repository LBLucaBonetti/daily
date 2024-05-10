package it.lbsoftware.daily.appuseractivations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.config.Constants;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AppUserActivationIntegrationTests extends DailyAbstractIntegrationTests {

  private static final String BASE_URL = Constants.ACTIVATION_PATH;

  @Value("${daily.base-uri}")
  private String baseUri;

  @Autowired private AppUserActivationRepository appUserActivationRepository;
  @Autowired private AppUserRepository appUserRepository;
  @Autowired private AppUserActivationServiceImpl appUserActivationService;

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should activate a new AppUser")
  void test1() throws Exception {
    // Given
    var appUserDto = new AppUserDto();
    appUserDto.setEmail("appuser@email.com");
    appUserDto.setPassword("password");
    appUserDto.setPasswordConfirmation("password");
    appUserDto.setLang("en-US");
    appUserDto.setFirstName("First name");
    appUserDto.setLastName("Last name");
    mockMvc
        .perform(
            post(Constants.SIGNUP_PATH)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", appUserDto.getEmail())
                .param("password", appUserDto.getPassword())
                .param("passwordConfirmation", appUserDto.getPasswordConfirmation())
                .param("lang", appUserDto.getLang())
                .param("firstName", appUserDto.getFirstName())
                .param("lastName", appUserDto.getLastName())
                .with(csrf()))
        .andExpect(status().isOk());
    var activationCode = appUserActivationRepository.findAll().get(0).getActivationCode();

    // When
    mockMvc
        .perform(get(BASE_URL, activationCode.toString()))
        .andExpect(status().isOk())
        .andExpect(view().name(Constants.LOGIN_VIEW));

    // Then
    assertTrue(
        appUserActivationRepository
            .findNonActivatedAndStillValidAppUserActivationFetchAppUser(activationCode)
            .isEmpty());
    var appUserActivation = appUserActivationRepository.findAll().get(0);
    assertNotNull(appUserActivation.getActivatedAt());
    assertTrue(appUserRepository.findAll().get(0).isEnabled());
  }

  @Test
  @DisplayName("Should get activation uri with injected value")
  void test2() {
    // Given
    var activationCode = UUID.randomUUID();
    var expectedUri = baseUri + "/" + Constants.ACTIVATIONS_VIEW + "/" + activationCode.toString();

    // When
    var res = appUserActivationService.getActivationUri(activationCode);

    // Then
    assertEquals(expectedUri, res);
  }
}
