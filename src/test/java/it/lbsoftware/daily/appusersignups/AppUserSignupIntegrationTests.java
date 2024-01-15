package it.lbsoftware.daily.appusersignups;

import static it.lbsoftware.daily.config.Constants.SIGNUP_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import it.lbsoftware.daily.appuseractivations.AppUserActivationRepository;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusersettings.AppUserSettingRepository;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import it.lbsoftware.daily.config.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("App user signup integration tests")
class AppUserSignupIntegrationTests extends DailyAbstractIntegrationTests {

  private static final String BASE_URL = Constants.SIGNUP_PATH;
  @Autowired private AppUserRepository appUserRepository;
  @Autowired private AppUserSettingRepository appUserSettingRepository;
  @Autowired private AppUserActivationRepository appUserActivationRepository;
  @Autowired private AppUserSettingService appUserSettingService;

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should sign a new AppUser up")
  void test1() throws Exception {
    // Given
    var appUserDto = new AppUserDto();
    appUserDto.setEmail("appuser@email.com");
    appUserDto.setPassword("password");
    appUserDto.setPasswordConfirmation("password");
    appUserDto.setLang("en-US");
    appUserDto.setFirstName("First name");
    appUserDto.setLastName("Last name");

    // When
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", appUserDto.getEmail())
                .param("password", appUserDto.getPassword())
                .param("passwordConfirmation", appUserDto.getPasswordConfirmation())
                .param("lang", appUserDto.getLang())
                .param("firstName", appUserDto.getFirstName())
                .param("lastName", appUserDto.getLastName())
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name(SIGNUP_VIEW));

    // Then
    assertEmailMessageCount(1);
    var appUserOptional =
        appUserRepository.findByEmailIgnoreCaseAndAuthProvider(
            appUserDto.getEmail(), AuthProvider.DAILY);
    assertTrue(appUserOptional.isPresent());
    var appUser = appUserOptional.get();
    var appUserSettingOptional = appUserSettingService.readAppUserSettings(appUser);
    assertTrue(appUserSettingOptional.isPresent());
    var appUserSetting = appUserSettingOptional.get();
    assertEquals(appUserDto.getLang(), appUserSetting.getLang());
    assertEquals(1, appUserActivationRepository.count());
    var appUserActivation = appUserActivationRepository.findAll().get(0);
    assertEquals(appUser, appUserActivation.getAppUser());
    // Should not be activated because of pending e-mail activation, so activatedAt should be null
    assertNull(appUserActivationRepository.findAll().get(0).getActivatedAt());
    assertEquals(1, appUserRepository.count());
    assertEquals(1, appUserSettingRepository.count());
    assertEquals(appUserDto.getEmail(), appUser.getEmail());
    assertEquals(appUserDto.getFirstName(), appUser.getFirstName());
    assertEquals(appUserDto.getLastName(), appUser.getLastName());
  }
}
