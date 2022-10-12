package it.lbsoftware.daily.appusers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@DisplayName("AppUserController unit tests")
class AppUserControllerTests extends DailyAbstractUnitTests {
  private static final String FULL_NAME = "name surname";
  private static final String EMAIL = "email@email.com";
  private AppUserController appUserController;

  @BeforeEach
  void beforeEach() {
    appUserController = new AppUserController();
  }

  @Test
  @DisplayName("Should not read info because of null appUser and return ok with empty strings")
  void test1() {
    // Given
    OidcUser appUser = null;

    // When
    ResponseEntity<InfoDto> res = appUserController.readInfo(appUser);

    // Then
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(res.getBody());
    assertEquals("", res.getBody().fullName());
    assertEquals("", res.getBody().email());
  }

  @Test
  @DisplayName("Should not read info because of null fullName and return ok with empty fullName")
  void test2() {
    // Given
    OidcUser appUser = mock(OidcUser.class);
    given(appUser.getFullName()).willReturn(null);
    given(appUser.getEmail()).willReturn(EMAIL);

    // When
    ResponseEntity<InfoDto> res = appUserController.readInfo(appUser);

    // Then
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(res.getBody());
    assertEquals("", res.getBody().fullName());
    assertEquals(EMAIL, res.getBody().email());
  }

  @Test
  @DisplayName("Should not read info because of null email and return ok with empty email")
  void test3() {
    // Given
    OidcUser appUser = mock(OidcUser.class);
    given(appUser.getFullName()).willReturn(FULL_NAME);
    given(appUser.getEmail()).willReturn(null);

    // When
    ResponseEntity<InfoDto> res = appUserController.readInfo(appUser);

    // Then
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(res.getBody());
    assertEquals(FULL_NAME, res.getBody().fullName());
    assertEquals("", res.getBody().email());
  }

  @Test
  @DisplayName(
      "Should not read info because of null fullName and email and return ok with empty fullName and email")
  void test4() {
    // Given
    OidcUser appUser = mock(OidcUser.class);
    given(appUser.getFullName()).willReturn(null);
    given(appUser.getEmail()).willReturn(null);

    // When
    ResponseEntity<InfoDto> res = appUserController.readInfo(appUser);

    // Then
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(res.getBody());
    assertEquals("", res.getBody().fullName());
    assertEquals("", res.getBody().email());
  }

  @Test
  @DisplayName("Should read info and return ok")
  void test5() {
    // Given
    OidcUser appUser = mock(OidcUser.class);
    given(appUser.getFullName()).willReturn(FULL_NAME);
    given(appUser.getEmail()).willReturn(EMAIL);

    // When
    ResponseEntity<InfoDto> res = appUserController.readInfo(appUser);

    // Then
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(res.getBody());
    assertEquals(FULL_NAME, res.getBody().fullName());
    assertEquals(EMAIL, res.getBody().email());
  }
}
