package it.lbsoftware.daily.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("ErrorConfiguration unit tests")
class ErrorConfigurationTests extends DailyAbstractUnitTests {

  private static final String ERROR_KEY = "error";
  private static final String ERROR_DEFAULT = "error.default";
  private ErrorConfiguration errorConfiguration;

  @BeforeEach
  void beforeEach() {
    errorConfiguration = new ErrorConfiguration();
  }

  @Test
  @DisplayName("Should handle error and return forward to /")
  void test1() {
    // Given
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);

    // When
    String res = errorConfiguration.handleError(httpServletResponse);

    // Then
    verify(httpServletResponse, times(1)).setStatus(HttpServletResponse.SC_OK);
    assertEquals("forward:/", res);
  }

  @Test
  @DisplayName(
      "Should handle error and return default response with status code if request has one")
  void test2() {
    // Given
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    given(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
        .willReturn(HttpStatus.BAD_REQUEST.value());

    // When
    ResponseEntity<Map<String, Object>> res = errorConfiguration.handleError(httpServletRequest);

    // Then
    assertNotNull(res);
    assertEquals(Map.of(ERROR_KEY, ERROR_DEFAULT), res.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
  }

  @Test
  @DisplayName(
      "Should handle error and return default response with internal server error status code if request does not have one")
  void test3() {
    // Given
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    given(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).willReturn(null);

    // When
    ResponseEntity<Map<String, Object>> res = errorConfiguration.handleError(httpServletRequest);

    // Then
    assertNotNull(res);
    assertEquals(Map.of(ERROR_KEY, ERROR_DEFAULT), res.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
  }

  @Test
  @DisplayName(
      "Should handle error and return default response with internal server error status code if request has an invalid one")
  void test4() {
    // Given
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    given(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).willReturn(-1);

    // When
    ResponseEntity<Map<String, Object>> res = errorConfiguration.handleError(httpServletRequest);

    // Then
    assertNotNull(res);
    assertEquals(Map.of(ERROR_KEY, ERROR_DEFAULT), res.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  @DisplayName(
      "Should handle error and return default response with status code if request has one")
  void test5(final String error) {
    // Given
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    given(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
        .willReturn(HttpStatus.BAD_REQUEST.value());
    given(httpServletRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE)).willReturn(error);

    // When
    ResponseEntity<Map<String, Object>> res = errorConfiguration.handleError(httpServletRequest);

    // Then
    assertNotNull(res);
    assertEquals(Map.of(ERROR_KEY, ERROR_DEFAULT), res.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
  }

  @Test
  @DisplayName(
      "Should handle error and return default response because of non-string error status code")
  void test6() {
    // Given
    Object error = new Object();
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    given(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
        .willReturn(HttpStatus.BAD_REQUEST.value());
    given(httpServletRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE)).willReturn(error);

    // When
    ResponseEntity<Map<String, Object>> res = errorConfiguration.handleError(httpServletRequest);

    // Then
    assertNotNull(res);
    assertEquals(Map.of(ERROR_KEY, ERROR_DEFAULT), res.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
  }

  @Test
  @DisplayName(
      "Should handle error and return specific response with status code if request has one")
  void test7() {
    // Given
    String errorKey = "error.specific";
    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    given(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
        .willReturn(HttpStatus.BAD_REQUEST.value());
    given(httpServletRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE)).willReturn(errorKey);

    // When
    ResponseEntity<Map<String, Object>> res = errorConfiguration.handleError(httpServletRequest);

    // Then
    assertNotNull(res);
    assertEquals(Map.of(ERROR_KEY, errorKey), res.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
  }
}
