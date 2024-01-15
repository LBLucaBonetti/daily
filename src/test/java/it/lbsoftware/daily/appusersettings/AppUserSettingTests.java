package it.lbsoftware.daily.appusersettings;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static it.lbsoftware.daily.appusersettings.AppUserSettingTestUtils.createAppUserSetting;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.bases.BaseEntity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppUserSettingTests extends DailyAbstractUnitTests {

  private static final AppUser APP_USER = createAppUser(UUID.randomUUID(), "appuser@email.com");
  private static final String LANG = "en-US";

  @Test
  @DisplayName("Should equal with itself")
  void test1() {
    // Given
    var appUserSetting = createAppUserSetting(LANG, APP_USER);

    // When
    boolean res = appUserSetting.equals(appUserSetting);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should not equal with different object")
  void test2() {
    // Given
    var appUserSetting = createAppUserSetting(LANG, APP_USER);

    // When
    boolean res = appUserSetting.equals("");

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should not equal when no id is present")
  void test3() {
    // Given
    var appUserSetting1 = createAppUserSetting(LANG, APP_USER);
    var appUserSetting2 = createAppUserSetting(LANG, APP_USER);

    // When
    boolean res = appUserSetting1.equals(appUserSetting2);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should equal with the same id")
  void test4() throws NoSuchFieldException, IllegalAccessException {
    // Given
    var appUserSetting1 = createAppUserSetting(LANG, APP_USER);
    var appUserSetting2 = createAppUserSetting(LANG, APP_USER);
    var id = 1L;
    var idField = BaseEntity.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(appUserSetting1, id);
    idField.set(appUserSetting2, id);
    idField.setAccessible(false);

    // When
    var res = appUserSetting1.equals(appUserSetting2);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should have equal hash when equal")
  void test5() throws NoSuchFieldException, IllegalAccessException {
    // Given
    var appUserSetting1 = createAppUserSetting(LANG, APP_USER);
    var appUserSetting2 = createAppUserSetting(LANG, APP_USER);
    var id = 1L;
    var idField = BaseEntity.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(appUserSetting1, id);
    idField.set(appUserSetting2, id);
    idField.setAccessible(false);

    // When
    var hash1 = appUserSetting1.hashCode();
    var hash2 = appUserSetting2.hashCode();

    // Then
    assertEquals(hash1, hash2);
  }
}
