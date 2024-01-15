package it.lbsoftware.daily.appuseractivations;

import static it.lbsoftware.daily.appuseractivations.AppUserActivationTestUtils.createAppUserActivation;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.bases.BaseEntity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppUserActivationTests extends DailyAbstractUnitTests {

  private static final AppUser APP_USER = createAppUser(UUID.randomUUID(), "appuser@email.com");
  private static final UUID ACTIVATION_CODE = UUID.randomUUID();

  @Test
  @DisplayName("Should equal with itself")
  void test1() {
    // Given
    var appUserActivation = createAppUserActivation(ACTIVATION_CODE, APP_USER);

    // When
    boolean res = appUserActivation.equals(appUserActivation);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should not equal with different object")
  void test2() {
    // Given
    var appUserActivation = createAppUserActivation(ACTIVATION_CODE, APP_USER);

    // When
    boolean res = appUserActivation.equals("");

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should not equal when no id is present")
  void test3() {
    // Given
    var appUserActivation1 = createAppUserActivation(ACTIVATION_CODE, APP_USER);
    var appUserActivation2 = createAppUserActivation(ACTIVATION_CODE, APP_USER);

    // When
    boolean res = appUserActivation1.equals(appUserActivation2);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should equal with the same id")
  void test4() throws NoSuchFieldException, IllegalAccessException {
    // Given
    var appUserActivation1 = createAppUserActivation(ACTIVATION_CODE, APP_USER);
    var appUserActivation2 = createAppUserActivation(ACTIVATION_CODE, APP_USER);
    var id = 1L;
    var idField = BaseEntity.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(appUserActivation1, id);
    idField.set(appUserActivation2, id);
    idField.setAccessible(false);

    // When
    var res = appUserActivation1.equals(appUserActivation2);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should have equal hash when equal")
  void test5() throws NoSuchFieldException, IllegalAccessException {
    // Given
    var appUserActivation1 = createAppUserActivation(ACTIVATION_CODE, APP_USER);
    var appUserActivation2 = createAppUserActivation(ACTIVATION_CODE, APP_USER);
    var id = 1L;
    var idField = BaseEntity.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(appUserActivation1, id);
    idField.set(appUserActivation2, id);
    idField.setAccessible(false);

    // When
    var hash1 = appUserActivation1.hashCode();
    var hash2 = appUserActivation2.hashCode();

    // Then
    assertEquals(hash1, hash2);
  }
}
