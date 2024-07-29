package it.lbsoftware.daily.frontend;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OperationResultTests extends DailyAbstractUnitTests {

  @Test
  @DisplayName("Should build ok instance with message")
  void test1() {
    // Given
    var messageKey = "messageKey";
    var messageValue = "messageValue";

    // When
    var res = OperationResult.ok(messageKey, messageValue);

    // Then
    assertTrue(res.isOk());
    assertFalse(res.isError());
    assertTrue(res.hasMessage());
  }

  @Test
  @DisplayName("Should build ok instance with no message")
  void test2() {
    // Given
    String messageKey = null;
    String messageValue = null;

    // When
    var res = OperationResult.ok(messageKey, messageValue);

    // Then
    assertTrue(res.isOk());
    assertFalse(res.isError());
    assertFalse(res.hasMessage());
  }

  @Test
  @DisplayName("Should build error instance with message")
  void test3() {
    // Given
    var messageKey = "messageKey";
    var messageValue = "messageValue";

    // When
    var res = OperationResult.error(messageKey, messageValue);

    // Then
    assertTrue(res.isError());
    assertFalse(res.isOk());
    assertTrue(res.hasMessage());
  }

  @Test
  @DisplayName("Should not perform action if has no message")
  void test4() {
    // Given
    Map<String, String> map = (Map<String, String>) mock(Map.class);

    // When
    var res = OperationResult.ok();
    res.ifHasMessage(map::put);

    // Then
    assertFalse(res.hasMessage());
    verify(map, times(0)).put(any(), any());
  }
}
