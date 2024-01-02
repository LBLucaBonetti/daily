package it.lbsoftware.daily.templates;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.validation.BindingResult;

class TemplateUtilsTests extends DailyAbstractUnitTests {

  static Stream<Arguments> test2() {
    // BindingResult, errorMessage
    BindingResult bindingResult = mock(BindingResult.class);
    String errorMessage = "errorMessage";
    return Stream.of(
        arguments(null, null), arguments(null, errorMessage), arguments(bindingResult, null));
  }

  @Test
  @DisplayName("Should not instantiate the class")
  void test1() throws NoSuchMethodException {
    // Given
    Constructor<TemplateUtils> viewUtilsConstructor = TemplateUtils.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(viewUtilsConstructor.getModifiers()));
    viewUtilsConstructor.setAccessible(true);

    // When
    InvocationTargetException res =
        assertThrows(InvocationTargetException.class, viewUtilsConstructor::newInstance);

    // Then
    assertNotNull(res);
    assertTrue(res.getCause() instanceof UnsupportedOperationException);
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when add error to view with null argument")
  void test2(BindingResult bindingResult, String errorMessage) {
    assertThrows(
        IllegalArgumentException.class,
        () -> TemplateUtils.addErrorToView(bindingResult, errorMessage));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when get oauth2 auth provider with null argument")
  void test3(String email) {
    assertThrows(IllegalArgumentException.class, () -> TemplateUtils.getOauth2AuthProvider(email));
  }
}
