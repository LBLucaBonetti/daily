package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_FULLNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class AppUserInfoControllerTests extends DailyAbstractUnitTests {

  @Mock private AppUserService appUserService;
  @Mock private OidcUser appUser;
  private AppUserInfoController appUserInfoController;

  @BeforeEach
  void beforeEach() {
    appUserInfoController = new AppUserInfoController(appUserService);
  }

  @Test
  @DisplayName("Should read info and return info")
  void test1() {
    // Given
    InfoDto info = new InfoDto(APP_USER_FULLNAME, APP_USER_EMAIL);
    given(appUserService.getAppUserInfo(appUser)).willReturn(info);

    // When
    ResponseEntity<InfoDto> res = appUserInfoController.readInfo(appUser);

    // Then
    verify(appUserService, times(1)).getAppUserInfo(appUser);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(info, res.getBody());
  }
}
