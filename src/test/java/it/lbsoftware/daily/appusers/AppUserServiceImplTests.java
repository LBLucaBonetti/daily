package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.UID_CLAIM;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@DisplayName("AppUserServiceImpl unit tests")
class AppUserServiceImplTests extends DailyAbstractUnitTests {

  private AppUserServiceImpl appUserService;
  private static final String UID_INVALID = "The OidcUser did not provide a valid unique id";
  private static final String APP_USER = "appUser";

  @BeforeEach
  void beforeEach() {
    appUserService = new AppUserServiceImpl();
  }

  @Test
  @DisplayName("Should throw when unique user id claim is null")
  void test1() {
    // Given
    Map<String, Object> idTokenClaims = new HashMap<>();
    idTokenClaims.put(UID_CLAIM, null);
    OidcUser appUser = createAppUser(idTokenClaims);

    // When
    IllegalArgumentException res =
        assertThrows(IllegalArgumentException.class, () -> appUserService.getUid(appUser));

    // Then
    assertEquals(UID_INVALID, res.getMessage());
  }

  @Test
  @DisplayName("Should throw when unique user id claim is empty")
  void test2() {
    // Given
    OidcUser appUser = createAppUser(Map.of(UID_CLAIM, ""));

    // When
    IllegalArgumentException res =
        assertThrows(IllegalArgumentException.class, () -> appUserService.getUid(appUser));

    // Then
    assertEquals(UID_INVALID, res.getMessage());
  }

  @Test
  @DisplayName("Should throw when unique user id claim is blank")
  void test3() {
    // Given
    OidcUser appUser = createAppUser(Map.of(UID_CLAIM, "   "));

    // When
    IllegalArgumentException res =
        assertThrows(IllegalArgumentException.class, () -> appUserService.getUid(appUser));

    // Then
    assertEquals(UID_INVALID, res.getMessage());
  }

  @Test
  @DisplayName("Should return unique user id when claim is filled")
  void test4() {
    // Given
    OidcUser appUser = createAppUser(Map.of(UID_CLAIM, APP_USER));

    // When
    String res = appUserService.getUid(appUser);

    // Then
    assertEquals(APP_USER, res);
  }
}
