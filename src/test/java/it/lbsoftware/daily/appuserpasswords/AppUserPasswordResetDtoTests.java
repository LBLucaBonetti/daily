package it.lbsoftware.daily.appuserpasswords;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.config.Constants;
import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppUserPasswordResetDtoTests extends DailyAbstractUnitTests {

  @Test
  @DisplayName("Should build instance with both params")
  void test1() {
    // Given
    var email = "appUser@gmail.com";
    var firstName = "FirstName";
    var passwordResetCode = UUID.randomUUID();
    var expiredAt =
        LocalDateTime.now().plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES);
    var password = "encodedPassword";
    var appUser = AppUser.builder().email(email).firstName(firstName).password(password).build();
    var appUserPasswordReset =
        AppUserPasswordReset.builder()
            .appUser(null)
            .passwordResetCode(passwordResetCode)
            .expiredAt(expiredAt)
            .build();

    // When
    var res = new AppUserPasswordResetDto(appUserPasswordReset, appUser);

    // Then
    assertEquals(StringUtils.toRootLowerCase(email), res.getAppUserEmail());
    assertEquals(firstName, res.getAppUserFirstName());
    assertEquals(passwordResetCode, res.getPasswordResetCode());
    assertEquals(expiredAt, res.getExpiredAt());
    assertEquals(password, res.getAppUserEncodedPassword());
  }

  @Test
  @DisplayName("Should build instance with a single param")
  void test2() {
    // Given
    var email = "appUser@gmail.com";
    var firstName = "FirstName";
    var passwordResetCode = UUID.randomUUID();
    var expiredAt =
        LocalDateTime.now().plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES);
    var password = "encodedPassword";
    var appUser = AppUser.builder().email(email).firstName(firstName).password(password).build();
    var appUserPasswordReset =
        AppUserPasswordReset.builder()
            .appUser(appUser)
            .passwordResetCode(passwordResetCode)
            .expiredAt(expiredAt)
            .build();

    // When
    var res = new AppUserPasswordResetDto(appUserPasswordReset);

    // Then
    assertEquals(StringUtils.toRootLowerCase(email), res.getAppUserEmail());
    assertEquals(firstName, res.getAppUserFirstName());
    assertEquals(passwordResetCode, res.getPasswordResetCode());
    assertEquals(expiredAt, res.getExpiredAt());
    assertEquals(password, res.getAppUserEncodedPassword());
  }

  @Test
  @DisplayName("Should throw with null AppUserPasswordReset (single-param constructor)")
  void test3() {
    // Given
    AppUserPasswordReset appUserPasswordReset = null;

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () -> new AppUserPasswordResetDto(appUserPasswordReset));

    // Then
    assertNotNull(res);
  }

  @Test
  @DisplayName("Should throw with null AppUserPasswordReset (both-params constructor)")
  void test4() {
    // Given
    AppUserPasswordReset appUserPasswordReset = null;
    var appUser = AppUser.builder().build();

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () -> new AppUserPasswordResetDto(appUserPasswordReset, appUser));

    // Then
    assertNotNull(res);
  }
}
