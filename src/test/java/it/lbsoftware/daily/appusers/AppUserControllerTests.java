package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_FULLNAME;
import static it.lbsoftware.daily.appusersettings.AppUserSettingTestUtils.createAppUserSettingDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusersettings.AppUserSettingDto;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@DisplayName("AppUserController unit tests")
class AppUserControllerTests extends DailyAbstractUnitTests {

  private static final UUID APP_USER = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final String LANG = "en-US";
  @Mock private AppUserService appUserService;
  @Mock private AppUserSettingService appUserSettingService;
  @Mock private OidcUser appUser;
  private AppUserController appUserController;

  @BeforeEach
  void beforeEach() {
    appUserController = new AppUserController(appUserService, appUserSettingService);
  }

  @Test
  @DisplayName("Should read info and return info")
  void test1() {
    // Given
    InfoDto info = new InfoDto(APP_USER_FULLNAME, APP_USER_EMAIL);
    given(appUserService.getAppUserInfo(appUser)).willReturn(info);

    // When
    ResponseEntity<InfoDto> res = appUserController.readInfo(appUser);

    // Then
    verify(appUserService, times(1)).getAppUserInfo(appUser);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(info, res.getBody());
  }

  @Test
  @DisplayName("Should not read app user settings and return not found")
  void test2() {
    // Given
    Optional<AppUserSettingDto> readAppUserSettings = Optional.empty();
    given(appUserService.getUuid(appUser)).willReturn(APP_USER);
    given(appUserSettingService.readAppUserSettings(APP_USER)).willReturn(readAppUserSettings);

    // When
    ResponseEntity<AppUserSettingDto> res = appUserController.readAppUserSettings(appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(appUserSettingService, times(1)).readAppUserSettings(APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should read app user settings and return ok")
  void test3() {
    // Given
    Optional<AppUserSettingDto> readAppUserSettings =
        Optional.of(createAppUserSettingDto(UUID.randomUUID(), LANG));
    given(appUserService.getUuid(appUser)).willReturn(APP_USER);
    given(appUserSettingService.readAppUserSettings(APP_USER)).willReturn(readAppUserSettings);

    // When
    ResponseEntity<AppUserSettingDto> res = appUserController.readAppUserSettings(appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(appUserSettingService, times(1)).readAppUserSettings(APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(readAppUserSettings.get(), res.getBody());
  }

  @Test
  @DisplayName("Should not update app user settings and return not found")
  void test4() {
    // Given
    AppUserSettingDto appUserSettings = createAppUserSettingDto(UUID.randomUUID(), LANG);
    Optional<AppUserSettingDto> updatedAppUserSettings = Optional.empty();
    given(appUserService.getUuid(appUser)).willReturn(APP_USER);
    given(appUserSettingService.updateAppUserSettings(appUserSettings, APP_USER))
        .willReturn(updatedAppUserSettings);

    // When
    ResponseEntity<AppUserSettingDto> res =
        appUserController.updateAppUserSettings(appUserSettings, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(appUserSettingService, times(1)).updateAppUserSettings(appUserSettings, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should update app user settings and return ok")
  void test5() {
    // Given
    AppUserSettingDto appUserSettings = createAppUserSettingDto(UUID.randomUUID(), LANG);
    Optional<AppUserSettingDto> updatedAppUserSettings = Optional.of(appUserSettings);
    given(appUserService.getUuid(appUser)).willReturn(APP_USER);
    given(appUserSettingService.updateAppUserSettings(appUserSettings, APP_USER))
        .willReturn(updatedAppUserSettings);

    // When
    ResponseEntity<AppUserSettingDto> res =
        appUserController.updateAppUserSettings(appUserSettings, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(appUserSettingService, times(1)).updateAppUserSettings(appUserSettings, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(updatedAppUserSettings.get(), res.getBody());
  }
}
