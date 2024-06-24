package it.lbsoftware.daily.appusers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appuserpasswords.AppUserPasswordReset;
import it.lbsoftware.daily.appuserpasswords.AppUserPasswordResetDto;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.config.Constants;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class AppUserUtilsTests extends DailyAbstractUnitTests {

  @Test
  @DisplayName("Should not instantiate the class")
  void test1() throws NoSuchMethodException {
    // Given
    Constructor<AppUserUtils> utils = AppUserUtils.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(utils.getModifiers()));
    utils.setAccessible(true);

    // When
    InvocationTargetException res =
        assertThrows(InvocationTargetException.class, utils::newInstance);
    utils.setAccessible(false);

    // Then
    assertNotNull(res);
    assertInstanceOf(UnsupportedOperationException.class, res.getCause());
  }

  @Test
  @DisplayName("Should return true when isDailyAuthProvider checking DAILY auth provider")
  void test2() {
    // Given
    var authProvider = AuthProvider.DAILY;

    // When
    var res = AppUserUtils.isDailyAuthProvider(authProvider);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should return false when isDailyAuthProvider checking non-DAILY auth provider")
  void test3() {
    // Given
    var authProvider = AuthProvider.GOOGLE;

    // When
    var res = AppUserUtils.isDailyAuthProvider(authProvider);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should return true when isOauth2AuthProvider checking Oauth2 auth provider")
  void test4() {
    // Given
    var authProvider = AuthProvider.GOOGLE;

    // When
    var res = AppUserUtils.isOauth2AuthProvider(authProvider);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should return false when isOauth2AuthProvider checking non-Oauth2 auth provider")
  void test5() {
    // Given
    var authProvider = AuthProvider.DAILY;

    // When
    var res = AppUserUtils.isOauth2AuthProvider(authProvider);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should return default first name when app user is null")
  void test6() {
    // Given
    AppUser appUser = null;

    // When
    var res = AppUserUtils.getFirstNameOrDefault(appUser);

    // Then
    assertEquals(Constants.APP_USER_UNSPECIFIED_NAME, res);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return default first name when app user first name is null")
  void test7(final String firstName) {
    // Given
    AppUser appUser = AppUser.builder().firstName(firstName).build();

    // When
    var res = AppUserUtils.getFirstNameOrDefault(appUser);

    // Then
    assertEquals(Constants.APP_USER_UNSPECIFIED_NAME, res);
  }

  @Test
  @DisplayName("Should return first name if app user is not null and first name is not blank")
  void test8() {
    // Given
    var firstName = "FirstName";
    var appUser = AppUser.builder().firstName(firstName).build();

    // When
    var res = AppUserUtils.getFirstNameOrDefault(appUser);

    // Then
    assertEquals(firstName, res);
  }

  @Test
  @DisplayName("Should return default first name when app user password reset dto is null")
  void test9() {
    // Given
    AppUserPasswordResetDto appUserPasswordResetDto = null;

    // When
    var res = AppUserUtils.getFirstNameOrDefault(appUserPasswordResetDto);

    // Then
    assertEquals(Constants.APP_USER_UNSPECIFIED_NAME, res);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName(
      "Should return default first name when app user password reset dto first name is null")
  void test10(final String firstName) {
    // Given
    var appUser = AppUser.builder().firstName(firstName).build();
    var appUserPasswordReset = AppUserPasswordReset.builder().appUser(appUser).build();
    var appUserPasswordResetDto = new AppUserPasswordResetDto(appUserPasswordReset);

    // When
    var res = AppUserUtils.getFirstNameOrDefault(appUserPasswordResetDto);

    // Then
    assertEquals(Constants.APP_USER_UNSPECIFIED_NAME, res);
  }

  @Test
  @DisplayName(
      "Should return first name if app user password reset dto is not null and first name is not blank")
  void test11() {
    // Given
    var firstName = "FirstName";
    var appUser = AppUser.builder().firstName(firstName).build();
    var appUserPasswordReset = AppUserPasswordReset.builder().appUser(appUser).build();
    var appUserPasswordResetDto = new AppUserPasswordResetDto(appUserPasswordReset);

    // When
    var res = AppUserUtils.getFirstNameOrDefault(appUserPasswordResetDto);

    // Then
    assertEquals(firstName, res);
  }
}
