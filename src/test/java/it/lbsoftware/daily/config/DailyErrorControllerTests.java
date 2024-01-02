package it.lbsoftware.daily.config;

import static it.lbsoftware.daily.config.Constants.ERROR_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.exceptions.DailyExceptionHandler;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

class DailyErrorControllerTests extends DailyAbstractUnitTests {

  private static final String ERROR_KEY = "error";
  private static final String ERROR_PREFIX = "daily.error.";
  private static final String ERROR_DEFAULT = ERROR_PREFIX + "default";
  private DailyErrorController dailyErrorController;
  @Mock private DailyExceptionHandler dailyExceptionHandler;

  @BeforeEach
  void beforeEach() {
    dailyErrorController = new DailyErrorController(dailyExceptionHandler);
  }

  @Test
  @DisplayName("Should handle error and return forward to /")
  void test1() {
    // Given
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    Authentication authentication = mock(Authentication.class);

    // When
    String res = dailyErrorController.handleError(httpServletResponse, authentication);

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
    given(dailyExceptionHandler.getExceptionBody(ERROR_DEFAULT))
        .willReturn(Map.of(ERROR_KEY, ERROR_DEFAULT));

    // When
    ResponseEntity<Map<String, Object>> res = dailyErrorController.handleError(httpServletRequest);

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
    given(dailyExceptionHandler.getExceptionBody(ERROR_DEFAULT))
        .willReturn(Map.of(ERROR_KEY, ERROR_DEFAULT));

    // When
    ResponseEntity<Map<String, Object>> res = dailyErrorController.handleError(httpServletRequest);

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
    given(dailyExceptionHandler.getExceptionBody(ERROR_DEFAULT))
        .willReturn(Map.of(ERROR_KEY, ERROR_DEFAULT));

    // When
    ResponseEntity<Map<String, Object>> res = dailyErrorController.handleError(httpServletRequest);

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
    given(dailyExceptionHandler.getExceptionBody(ERROR_DEFAULT))
        .willReturn(Map.of(ERROR_KEY, ERROR_DEFAULT));

    // When
    ResponseEntity<Map<String, Object>> res = dailyErrorController.handleError(httpServletRequest);

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
    given(dailyExceptionHandler.getExceptionBody(ERROR_DEFAULT))
        .willReturn(Map.of(ERROR_KEY, ERROR_DEFAULT));

    // When
    ResponseEntity<Map<String, Object>> res = dailyErrorController.handleError(httpServletRequest);

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
    given(dailyExceptionHandler.getExceptionBody(errorKey)).willReturn(Map.of(ERROR_KEY, errorKey));

    // When
    ResponseEntity<Map<String, Object>> res = dailyErrorController.handleError(httpServletRequest);

    // Then
    assertNotNull(res);
    assertEquals(Map.of(ERROR_KEY, errorKey), res.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
  }

  @Test
  @DisplayName("Should not handle error and return error view when not authenticated")
  void test8() {
    // Given
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    Authentication authentication = null;

    // When
    var res = dailyErrorController.handleError(httpServletResponse, authentication);

    // Then
    assertEquals(ERROR_VIEW, res);
  }
}
