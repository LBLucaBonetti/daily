package it.lbsoftware.daily.appusersettings;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static it.lbsoftware.daily.appusersettings.AppUserSettingTestUtils.createAppUserSettingDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class AppUserSettingControllerTests extends DailyAbstractUnitTests {

  private static final String EMAIL = "appuser@email.com";
  private static final UUID UNIQUE_ID = UUID.randomUUID();
  private static final AppUser APP_USER = createAppUser(UNIQUE_ID, EMAIL);
  private static final String LANG = "en-US";
  @Mock private AppUserService appUserService;
  @Mock private AppUserSettingService appUserSettingService;
  @Mock private OidcUser appUser;
  private AppUserSettingController appUserSettingController;

  @BeforeEach
  void beforeEach() {
    appUserSettingController = new AppUserSettingController(appUserService, appUserSettingService);
  }

  @Test
  @DisplayName("Should not read app user settings and return not found")
  void test1() {
    // Given
    Optional<AppUserSettingDto> readAppUserSettings = Optional.empty();
    given(appUserService.getAppUser(appUser)).willReturn(APP_USER);
    given(appUserSettingService.readAppUserSettings(APP_USER)).willReturn(readAppUserSettings);

    // When
    ResponseEntity<AppUserSettingDto> res = appUserSettingController.readAppUserSettings(appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(appUserSettingService, times(1)).readAppUserSettings(APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should read app user settings and return ok")
  void test2() {
    // Given
    Optional<AppUserSettingDto> readAppUserSettings =
        Optional.of(createAppUserSettingDto(UUID.randomUUID(), LANG));
    given(appUserService.getAppUser(appUser)).willReturn(APP_USER);
    given(appUserSettingService.readAppUserSettings(APP_USER)).willReturn(readAppUserSettings);

    // When
    ResponseEntity<AppUserSettingDto> res = appUserSettingController.readAppUserSettings(appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(appUserSettingService, times(1)).readAppUserSettings(APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(readAppUserSettings.get(), res.getBody());
  }

  @Test
  @DisplayName("Should not update app user settings and return not found")
  void test3() {
    // Given
    AppUserSettingDto appUserSettings = createAppUserSettingDto(UUID.randomUUID(), LANG);
    Optional<AppUserSettingDto> updatedAppUserSettings = Optional.empty();
    given(appUserService.getAppUser(appUser)).willReturn(APP_USER);
    given(appUserSettingService.updateAppUserSettings(appUserSettings, APP_USER))
        .willReturn(updatedAppUserSettings);

    // When
    ResponseEntity<AppUserSettingDto> res =
        appUserSettingController.updateAppUserSettings(appUserSettings, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(appUserSettingService, times(1)).updateAppUserSettings(appUserSettings, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should update app user settings and return ok")
  void test4() {
    // Given
    AppUserSettingDto appUserSettings = createAppUserSettingDto(UUID.randomUUID(), LANG);
    Optional<AppUserSettingDto> updatedAppUserSettings = Optional.of(appUserSettings);
    given(appUserService.getAppUser(appUser)).willReturn(APP_USER);
    given(appUserSettingService.updateAppUserSettings(appUserSettings, APP_USER))
        .willReturn(updatedAppUserSettings);

    // When
    ResponseEntity<AppUserSettingDto> res =
        appUserSettingController.updateAppUserSettings(appUserSettings, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(appUserSettingService, times(1)).updateAppUserSettings(appUserSettings, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(updatedAppUserSettings.get(), res.getBody());
  }
}
