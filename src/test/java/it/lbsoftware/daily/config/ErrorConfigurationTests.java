package it.lbsoftware.daily.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ErrorConfiguration unit tests")
class ErrorConfigurationTests extends DailyAbstractUnitTests {

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
}
