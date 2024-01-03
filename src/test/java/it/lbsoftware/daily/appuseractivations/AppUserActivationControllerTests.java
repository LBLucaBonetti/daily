package it.lbsoftware.daily.appuseractivations;

import static it.lbsoftware.daily.config.Constants.LOGIN_VIEW;
import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.config.Constants;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

class AppUserActivationControllerTests extends DailyAbstractUnitTests {

  @Mock private AppUserService appUserService;
  private AppUserActivationController appUserActivationController;

  @BeforeEach
  void beforeEach() {
    appUserActivationController = new AppUserActivationController(appUserService);
  }

  @Test
  @DisplayName("Should redirect if authenticated when get activate")
  void test1() {
    // Given
    UUID activationCode = UUID.randomUUID();
    Model model = mock(Model.class);
    Authentication authentication = mock(Authentication.class);

    // When
    var res = appUserActivationController.activate(activationCode, model, authentication);

    // Then
    assertEquals(REDIRECT, res);
    verify(appUserActivationController, times(0)).activate(any(), any(), any());
  }

  @Test
  @DisplayName("Should return login with failure when activation fails, when get activate")
  void test2() {
    // Given
    UUID activationCode = UUID.randomUUID();
    Model model = mock(Model.class);
    Authentication authentication = null;
    when(appUserService.activate(activationCode)).thenReturn(false);

    // When
    var res = appUserActivationController.activate(activationCode, model, authentication);

    // Then
    verify(model, times(1)).addAttribute(eq(Constants.ACTIVATION_CODE_FAILURE), anyString());
    assertEquals(LOGIN_VIEW, res);
  }

  @Test
  @DisplayName("Should return login with success when activation succeeds, when get activate")
  void test3() {
    // Given
    UUID activationCode = UUID.randomUUID();
    Model model = mock(Model.class);
    Authentication authentication = null;
    when(appUserService.activate(activationCode)).thenReturn(true);

    // When
    var res = appUserActivationController.activate(activationCode, model, authentication);

    // Then
    verify(model, times(1)).addAttribute(eq(Constants.ACTIVATION_CODE_SUCCESS), anyString());
    assertEquals(LOGIN_VIEW, res);
  }
}
