package it.lbsoftware.daily.appuserlogins;

import static it.lbsoftware.daily.config.Constants.LOGIN_VIEW;
import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.config.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

class AppUserLoginControllerTests extends DailyAbstractUnitTests {

  private AppUserLoginController appUserLoginController;

  @BeforeEach
  void beforeEach() {
    appUserLoginController = new AppUserLoginController();
  }

  @Test
  @DisplayName("Should redirect if authenticated when get login")
  void test1() {
    // Given
    Authentication authentication = mock(Authentication.class);
    var request = mock(HttpServletRequest.class);
    var model = mock(Model.class);
    given(request.getParameter(anyString())).willReturn(null);

    // When
    var res = appUserLoginController.login(authentication, request, model);

    // Then
    assertEquals(REDIRECT, res);
  }

  @Test
  @DisplayName("Should return login when get login")
  void test2() {
    // Given
    Authentication authentication = null;
    var request = mock(HttpServletRequest.class);
    var model = mock(Model.class);
    given(request.getParameter(anyString())).willReturn(null);

    // When
    var res = appUserLoginController.login(authentication, request, model);

    // Then
    assertEquals(LOGIN_VIEW, res);
  }

  @Test
  @DisplayName(
      "Should add invalid-credentials error to the model when get login and an authentication exception occurred")
  void test3() {
    // Given
    Authentication authentication = null;
    var request = mock(HttpServletRequest.class);
    var model = new ExtendedModelMap();
    var parameterValue = "";
    given(request.getParameter(anyString())).willReturn(parameterValue);

    // When
    appUserLoginController.login(authentication, request, model);

    // Then
    assertNotNull(model.getAttribute(Constants.INVALID_CREDENTIALS_ERROR));
  }
}
