package it.lbsoftware.daily.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConstantsTests extends DailyAbstractUnitTests {

  @Test
  @DisplayName("Should not instantiate the class")
  void test1() throws NoSuchMethodException {
    // Given
    Constructor<Constants> constantsConstructor = Constants.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constantsConstructor.getModifiers()));
    constantsConstructor.setAccessible(true);

    // When
    InvocationTargetException res =
        assertThrows(InvocationTargetException.class, constantsConstructor::newInstance);

    // Then
    assertNotNull(res);
    assertTrue(res.getCause() instanceof UnsupportedOperationException);
  }
}
