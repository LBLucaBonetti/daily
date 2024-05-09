package it.lbsoftware.daily.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    args = {
      "--" + Constants.DAILY_COOKIE_CSRF_ENHANCED_SECURITY_KEY + "=true",
    })
class CookieCsrfTokenRepositoryConfigurationTests extends DailyAbstractIntegrationTests {

  @Autowired private CookieCsrfTokenRepository cookieCsrfTokenRepository;
  @Captor private ArgumentCaptor<Cookie> csrfTokenCookieCaptor;

  @Test
  @DisplayName("Should build a CSRF token with enhanced security")
  void test1() {
    // Given
    var mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest.setSecure(false);
    var mockHttpServletResponse = mock(HttpServletResponse.class);
    var token = UUID.randomUUID().toString();
    var defaultCsrfHeaderName = "X-CSRF-TOKEN";
    var defaultCsrfInputFieldName = "_csrf";
    var csrfCookieName = "XSRF-TOKEN";
    var csrfToken = new DefaultCsrfToken(defaultCsrfHeaderName, defaultCsrfInputFieldName, token);

    // When
    cookieCsrfTokenRepository.saveToken(csrfToken, mockHttpServletRequest, mockHttpServletResponse);

    // Then
    verify(mockHttpServletResponse).addCookie(csrfTokenCookieCaptor.capture());
    var csrfTokenCookie = csrfTokenCookieCaptor.getValue();
    assertEquals(csrfCookieName, csrfTokenCookie.getName());
    assertEquals(token, csrfTokenCookie.getValue());
    assertTrue(csrfTokenCookie.getSecure());
    assertEquals("Strict", csrfTokenCookie.getAttribute("SameSite"));
  }
}
