package it.lbsoftware.daily.frontend;

import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static it.lbsoftware.daily.frontend.TemplateUtils.DEFAULT_COMPROMISED_PASSWORD_ERROR_MESSAGE;
import static it.lbsoftware.daily.frontend.TemplateUtils.DEFAULT_INVALID_CREDENTIALS_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.validation.BindingResult;

class TemplateUtilsTests extends DailyAbstractUnitTests {

  private static Stream<Arguments> test2() {
    // BindingResult, errorMessage
    BindingResult bindingResult = mock(BindingResult.class);
    String errorMessage = "errorMessage";
    return Stream.of(
        arguments(null, null), arguments(null, errorMessage), arguments(bindingResult, null));
  }

  private static Stream<Arguments> test5() {
    // ServletRequest, parameterName
    ServletRequest servletRequest = mock(ServletRequest.class);
    String parameterName = "parameterName";
    return Stream.of(
        arguments(null, null), arguments(null, parameterName), arguments(servletRequest, null));
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
    utils.setAccessible(false);

    // Then
    assertNotNull(res);
    assertInstanceOf(UnsupportedOperationException.class, res.getCause());
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when add error to view with null argument")
  void test2(final BindingResult bindingResult, final String errorMessage) {
    assertThrows(
        IllegalArgumentException.class,
        () -> TemplateUtils.addErrorToView(bindingResult, errorMessage));
  }

  @Test
  @DisplayName("Should not redirect and return empty optional")
  void test3() {
    // Given
    Authentication authentication = null;

    // When
    var res = TemplateUtils.redirectIfAuthenticated(authentication);

    // Then
    assertTrue(res.isEmpty());
  }

  @Test
  @DisplayName("Should redirect and return redirect optional")
  void test4() {
    // Given
    Authentication authentication = mock(Authentication.class);

    // When
    var res = TemplateUtils.redirectIfAuthenticated(authentication);

    // Then
    assertTrue(res.isPresent());
    assertEquals(REDIRECT, res.get());
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName(
      "Should return false when checking if request has non-null param with null arguments")
  void test5(final ServletRequest request, final String parameterName) {
    assertFalse(TemplateUtils.hasNonNullParameter(request, parameterName));
  }

  @Test
  @DisplayName(
      "Should return false when checking if request has non-null param and request has not that param")
  void test6() {
    // Given
    var parameterName = "parameterName";
    var request = mock(ServletRequest.class);
    given(request.getParameter(parameterName)).willReturn(null);

    // When
    var res = TemplateUtils.hasNonNullParameter(request, parameterName);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName(
      "Should return true when checking if request has non-null param and request has that param")
  void test7() {
    // Given
    var parameterName = "parameterName";
    var parameterValue = "";
    var request = mock(ServletRequest.class);
    given(request.getParameter(parameterName)).willReturn(parameterValue);

    // When
    var res = TemplateUtils.hasNonNullParameter(request, parameterName);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName(
      "Should return default invalid-credentials error message when getting invalid-credentials error message with null session")
  void test8() {
    // Given
    var request = mock(HttpServletRequest.class);
    given(request.getSession(false)).willReturn(null);

    // When
    var res = TemplateUtils.getInvalidCredentialsErrorMessage(request);

    // Then
    assertEquals(DEFAULT_INVALID_CREDENTIALS_ERROR_MESSAGE, res);
  }

  @Test
  @DisplayName(
      "Should return default invalid-credentials error message when getting invalid-credentials error message with null authentication exception session attribute")
  void test9() {
    // Given
    var request = mock(HttpServletRequest.class);
    var session = mock(HttpSession.class);
    given(request.getSession(false)).willReturn(session);
    given(session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)).willReturn(null);

    // When
    var res = TemplateUtils.getInvalidCredentialsErrorMessage(request);

    // Then
    assertEquals(DEFAULT_INVALID_CREDENTIALS_ERROR_MESSAGE, res);
  }

  @Test
  @DisplayName(
      "Should return default invalid-credentials error message when getting invalid-credentials error message with non-specific authentication exception session attribute")
  void test10() {
    // Given
    var request = mock(HttpServletRequest.class);
    var session = mock(HttpSession.class);
    given(request.getSession(false)).willReturn(session);
    given(session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION))
        .willReturn(new RuntimeException());

    // When
    var res = TemplateUtils.getInvalidCredentialsErrorMessage(request);

    // Then
    assertEquals(DEFAULT_INVALID_CREDENTIALS_ERROR_MESSAGE, res);
  }

  @Test
  @DisplayName(
      "Should return default compromised password error message when getting invalid-credentials error message with compromised password authentication exception session attribute")
  void test11() {
    // Given
    var request = mock(HttpServletRequest.class);
    var session = mock(HttpSession.class);
    given(request.getSession(false)).willReturn(session);
    given(session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION))
        .willReturn(new CompromisedPasswordException("Compromised password"));

    // When
    var res = TemplateUtils.getInvalidCredentialsErrorMessage(request);

    // Then
    assertEquals(DEFAULT_COMPROMISED_PASSWORD_ERROR_MESSAGE, res);
  }
}
