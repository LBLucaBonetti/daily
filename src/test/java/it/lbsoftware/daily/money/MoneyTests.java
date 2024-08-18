package it.lbsoftware.daily.money;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static it.lbsoftware.daily.money.MoneyTestUtils.createMoney;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.money.Money.OperationType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MoneyTests extends DailyAbstractUnitTests {

  private static final AppUser APP_USER = createAppUser(UUID.randomUUID(), "appuser@email.com");
  private static final String DESCRIPTION = "description";

  @Test
  @DisplayName("Should equal with itself")
  void test1() {
    // Given
    var money =
        createMoney(
            LocalDate.now(),
            BigDecimal.ZERO,
            OperationType.INCOME,
            DESCRIPTION,
            Collections.emptySet(),
            APP_USER);

    // When
    boolean res = money.equals(money);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should not equal with different object")
  void test2() {
    // Given
    var money =
        createMoney(
            LocalDate.now(),
            BigDecimal.ZERO,
            OperationType.INCOME,
            DESCRIPTION,
            Collections.emptySet(),
            APP_USER);

    // When
    boolean res = money.equals("");

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should not equal when no id is present")
  void test3() {
    // Given
    var money1 =
        createMoney(
            LocalDate.now(),
            BigDecimal.ZERO,
            OperationType.INCOME,
            DESCRIPTION,
            Collections.emptySet(),
            APP_USER);
    var money2 =
        createMoney(
            LocalDate.now(),
            BigDecimal.ZERO,
            OperationType.INCOME,
            DESCRIPTION,
            Collections.emptySet(),
            APP_USER);

    // When
    boolean res = money1.equals(money2);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should have equal hash when equal")
  void test4() throws NoSuchFieldException, IllegalAccessException {
    // Given
    var money1 =
        createMoney(
            LocalDate.now(),
            BigDecimal.ZERO,
            OperationType.INCOME,
            DESCRIPTION,
            Collections.emptySet(),
            APP_USER);
    var money2 =
        createMoney(
            LocalDate.now(),
            BigDecimal.ZERO,
            OperationType.INCOME,
            DESCRIPTION,
            Collections.emptySet(),
            APP_USER);
    var id = 1L;
    var idField = BaseEntity.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(money1, id);
    idField.set(money2, id);
    idField.setAccessible(false);

    // When
    var hash1 = money1.hashCode();
    var hash2 = money2.hashCode();

    // Then
    assertEquals(hash1, hash2);
  }
}
