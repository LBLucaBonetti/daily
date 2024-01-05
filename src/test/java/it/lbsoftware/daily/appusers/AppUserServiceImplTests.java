package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_FULLNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class AppUserServiceImplTests extends DailyAbstractUnitTests {

  @Mock private AppUserRepository appUserRepository;
  @Mock private AppUserActivationService appUserActivationService;
  private AppUserServiceImpl appUserService;

  @BeforeEach
  void beforeEach() {
    appUserService = new AppUserServiceImpl(appUserRepository, appUserActivationService);
  }

  @Test
  @DisplayName("Should not get uuid and throw")
  void test1() {
    // Given
    OidcUser principal = mock(OidcUser.class);
    given(principal.getEmail()).willReturn(APP_USER_EMAIL);
    given(principal.getFullName()).willReturn(APP_USER_FULLNAME);
    given(appUserRepository.findByEmailIgnoreCase(APP_USER_EMAIL)).willReturn(Optional.empty());

    // When
    var res = assertThrows(NoSuchElementException.class, () -> appUserService.getUuid(principal));

    // Then
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(APP_USER_EMAIL);
    assertNotNull(res.getMessage());
  }

  @Test
  @DisplayName("Should get uuid and return uuid")
  void test2() {
    // Given
    OidcUser principal = mock(OidcUser.class);
    AppUser appUser = mock(AppUser.class);
    UUID uuid = UUID.randomUUID();
    given(principal.getEmail()).willReturn(APP_USER_EMAIL);
    given(principal.getFullName()).willReturn(APP_USER_FULLNAME);
    given(appUserRepository.findByEmailIgnoreCase(APP_USER_EMAIL)).willReturn(Optional.of(appUser));
    given(appUser.getUuid()).willReturn(uuid);

    // When
    var res = appUserService.getUuid(principal);

    // Then
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(APP_USER_EMAIL);
    assertEquals(uuid, res);
  }

  @Test
  @DisplayName("Should get app user info with OidcUser")
  void test3() {
    // Given
    OidcUser principal = mock(OidcUser.class);
    given(principal.getEmail()).willReturn(APP_USER_EMAIL);
    given(principal.getFullName()).willReturn(APP_USER_FULLNAME);

    // When
    var res = appUserService.getAppUserInfo(principal);

    // Then
    assertEquals(APP_USER_EMAIL, res.email());
    assertEquals(APP_USER_FULLNAME, res.fullName());
  }

  @Test
  @DisplayName("Should get app user info with AppUserDetails")
  void test4() {
    // Given
    AppUserDetails principal = mock(AppUserDetails.class);
    given(principal.getUsername()).willReturn(APP_USER_EMAIL);
    given(principal.getFullname()).willReturn(APP_USER_FULLNAME);

    // When
    var res = appUserService.getAppUserInfo(principal);

    // Then
    assertEquals(APP_USER_EMAIL, res.email());
    assertEquals(APP_USER_FULLNAME, res.fullName());
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when get uuid with null argument")
  void test5(Object principal) {
    assertThrows(IllegalArgumentException.class, () -> appUserService.getUuid(principal));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when get app user info with null argument")
  void test6(Object principal) {
    assertThrows(IllegalArgumentException.class, () -> appUserService.getAppUserInfo(principal));
  }

  @Test
  @DisplayName("Should throw when get app user info and app user is not recognized")
  void test7() {
    // Given
    String appUser = "appUser";

    // When and then
    assertThrows(IllegalStateException.class, () -> appUserService.getAppUserInfo(appUser));
  }
}
