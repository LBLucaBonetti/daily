package it.lbsoftware.daily.appuserremovers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.bases.BaseEntity;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppUserRemovalInformationTests extends DailyAbstractUnitTests {

  @Test
  @DisplayName("Should equal with itself")
  void test1() {
    // Given
    var appUserRemovalInformation = new AppUserRemovalInformation();

    // When
    boolean res = appUserRemovalInformation.equals(appUserRemovalInformation);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should not equal with different object")
  void test2() {
    // Given
    var appUserRemovalInformation = new AppUserRemovalInformation();

    // When
    boolean res = appUserRemovalInformation.equals("");

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should not equal when no id is present")
  void test3() {
    // Given
    var notifiedAt = LocalDateTime.now();
    var failures = 0;
    var appUserRemovalInformation1 =
        AppUserRemovalInformation.builder().notifiedAt(notifiedAt).failures(failures).build();
    var appUserRemovalInformation2 =
        AppUserRemovalInformation.builder().notifiedAt(notifiedAt).failures(failures).build();

    // When
    var res = appUserRemovalInformation1.equals(appUserRemovalInformation2);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should have equal hash when equal")
  void test4() throws NoSuchFieldException, IllegalAccessException {
    // Given
    var notifiedAt = LocalDateTime.now();
    var failures = 0;
    var appUserRemovalInformation1 =
        AppUserRemovalInformation.builder().notifiedAt(notifiedAt).failures(failures).build();
    var appUserRemovalInformation2 =
        AppUserRemovalInformation.builder().notifiedAt(notifiedAt).failures(failures).build();
    var id = 1L;
    var idField = BaseEntity.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(appUserRemovalInformation1, id);
    idField.set(appUserRemovalInformation2, id);
    idField.setAccessible(false);

    // When
    var hash1 = appUserRemovalInformation1.hashCode();
    var hash2 = appUserRemovalInformation2.hashCode();

    // Then
    assertEquals(hash1, hash2);
  }
}
