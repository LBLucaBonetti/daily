package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.config.Constants.LOGIN_VIEW;
import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static it.lbsoftware.daily.config.Constants.SEND_PASSWORD_RESET_NOTIFICATION_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

class AppUserPasswordControllerTests extends DailyAbstractUnitTests {

  @Mock private AppUserPasswordService appUserPasswordService;
  private AppUserPasswordController appUserPasswordController;

  @BeforeEach
  void beforeEach() {
    appUserPasswordController = new AppUserPasswordController(appUserPasswordService);
  }

  @Test
  @DisplayName("Should redirect if authenticated when get send password reset notification")
  void test1() {
    // Given
    var model = mock(Model.class);
    var authentication = mock(Authentication.class);

    // When
    var res = appUserPasswordController.sendPasswordResetNotification(model, authentication);

    // Then
    verify(model, times(0)).addAttribute(any(), any());
    assertEquals(REDIRECT, res);
  }

  @Test
  @DisplayName(
      "Should return send password reset notification view when get send password reset notification")
  void test2() {
    // Given
    var model = mock(Model.class);
    Authentication authentication = null;

    // When
    var res = appUserPasswordController.sendPasswordResetNotification(model, authentication);

    // Then
    verify(model, times(1)).addAttribute(any(), any());
    assertEquals(SEND_PASSWORD_RESET_NOTIFICATION_VIEW, res);
  }

  @Test
  @DisplayName("Should redirect if authenticated when post send password reset notification")
  void test3() {
    // Given
    var passwordResetNotificationDto = mock(PasswordResetNotificationDto.class);
    var bindingResult = mock(BindingResult.class);
    var model = mock(Model.class);
    var authentication = mock(Authentication.class);

    // When
    var res =
        appUserPasswordController.sendPasswordResetNotification(
            passwordResetNotificationDto, bindingResult, model, authentication);

    // Then
    verify(appUserPasswordService, times(0)).sendPasswordResetNotification(any(), any());
    assertEquals(REDIRECT, res);
  }

  @Test
  @DisplayName(
      "Should return send password reset notification view when post send password reset notification with binding errors")
  void test4() {
    // Given
    var passwordResetNotificationDto = mock(PasswordResetNotificationDto.class);
    var bindingResult = mock(BindingResult.class);
    var model = mock(Model.class);
    Authentication authentication = null;
    given(bindingResult.hasErrors()).willReturn(true);

    // When
    var res =
        appUserPasswordController.sendPasswordResetNotification(
            passwordResetNotificationDto, bindingResult, model, authentication);

    // Then
    verify(appUserPasswordService, times(0)).sendPasswordResetNotification(any(), any());
    assertEquals(SEND_PASSWORD_RESET_NOTIFICATION_VIEW, res);
  }

  @Test
  @DisplayName(
      "Should call service and return login view when post send password reset notification without binding errors")
  void test5() {
    // Given
    var passwordResetNotificationDto = mock(PasswordResetNotificationDto.class);
    var bindingResult = mock(BindingResult.class);
    var model = mock(Model.class);
    Authentication authentication = null;
    given(bindingResult.hasErrors()).willReturn(false);

    // When
    var res =
        appUserPasswordController.sendPasswordResetNotification(
            passwordResetNotificationDto, bindingResult, model, authentication);

    // Then
    verify(appUserPasswordService, times(1))
        .sendPasswordResetNotification(passwordResetNotificationDto, model);
    assertEquals(LOGIN_VIEW, res);
  }
}
