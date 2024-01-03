package it.lbsoftware.daily.templates;

import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
import org.springframework.security.core.Authentication;
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
    Constructor<TemplateUtils> utils = TemplateUtils.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(utils.getModifiers()));
    utils.setAccessible(true);

    // When
    InvocationTargetException res =
        assertThrows(InvocationTargetException.class, utils::newInstance);

    // Then
    assertNotNull(res);
    assertInstanceOf(UnsupportedOperationException.class, res.getCause());
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

  @Test
  @DisplayName("Should not redirect and return empty optional")
  void test4() {
    // Given
    Authentication authentication = null;

    // When
    var res = TemplateUtils.redirectIfAuthenticated(authentication);

    // Then
    assertTrue(res.isEmpty());
  }

  @Test
  @DisplayName("Should redirect and return redirect optional")
  void test5() {
    // Given
    Authentication authentication = mock(Authentication.class);

    // When
    var res = TemplateUtils.redirectIfAuthenticated(authentication);

    // Then
    assertTrue(res.isPresent());
    assertEquals(REDIRECT, res.get());
  }
}
