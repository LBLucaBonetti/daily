package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.appuserpasswords.AppUserPasswordResetTestUtils.createAppUserPasswordReset;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.bases.BaseEntity;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppUserPasswordResetTests extends DailyAbstractUnitTests {

  private static final AppUser APP_USER = createAppUser(UUID.randomUUID(), "appuser@email.com");
  private static final UUID PASSWORD_RESET_CODE = UUID.randomUUID();
  private static final LocalDateTime EXPIRED_AT = LocalDateTime.now().plusDays(1);

  @Test
  @DisplayName("Should equal with itself")
  void test1() {
    // Given
    var appUserPasswordReset =
        createAppUserPasswordReset(PASSWORD_RESET_CODE, EXPIRED_AT, APP_USER);

    // When
    boolean res = appUserPasswordReset.equals(appUserPasswordReset);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should not equal with different object")
  void test2() {
    // Given
    var appUserPasswordReset =
        createAppUserPasswordReset(PASSWORD_RESET_CODE, EXPIRED_AT, APP_USER);

    // When
    boolean res = appUserPasswordReset.equals("");

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should not equal when no id is present")
  void test3() {
    // Given
    var appUserPasswordReset1 =
        createAppUserPasswordReset(PASSWORD_RESET_CODE, EXPIRED_AT, APP_USER);
    var appUserPasswordReset2 =
        createAppUserPasswordReset(PASSWORD_RESET_CODE, EXPIRED_AT, APP_USER);

    // When
    boolean res = appUserPasswordReset1.equals(appUserPasswordReset2);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should equal with the same id")
  void test4() throws NoSuchFieldException, IllegalAccessException {
    // Given
    var appUserPasswordReset1 =
        createAppUserPasswordReset(PASSWORD_RESET_CODE, EXPIRED_AT, APP_USER);
    var appUserPasswordReset2 =
        createAppUserPasswordReset(PASSWORD_RESET_CODE, EXPIRED_AT, APP_USER);
    var id = 1L;
    var idField = BaseEntity.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(appUserPasswordReset1, id);
    idField.set(appUserPasswordReset2, id);
    idField.setAccessible(false);

    // When
    var res = appUserPasswordReset1.equals(appUserPasswordReset2);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should have equal hash when equal")
  void test5() throws NoSuchFieldException, IllegalAccessException {
    // Given
    var appUserPasswordReset1 =
        createAppUserPasswordReset(PASSWORD_RESET_CODE, EXPIRED_AT, APP_USER);
    var appUserPasswordReset2 =
        createAppUserPasswordReset(PASSWORD_RESET_CODE, EXPIRED_AT, APP_USER);
    var id = 1L;
    var idField = BaseEntity.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(appUserPasswordReset1, id);
    idField.set(appUserPasswordReset2, id);
    idField.setAccessible(false);

    // When
    var hash1 = appUserPasswordReset1.hashCode();
    var hash2 = appUserPasswordReset2.hashCode();

    // Then
    assertEquals(hash1, hash2);
  }
}
