package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.config.Constants.LOGIN_VIEW;
import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static it.lbsoftware.daily.config.Constants.SEND_PASSWORD_RESET_NOTIFICATION_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.config.Constants;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

class AppUserPasswordControllerTests extends DailyAbstractUnitTests {

  @Mock private AppUserPasswordService appUserPasswordService;
  @Mock private AppUserPasswordResetService appUserPasswordResetService;
  private AppUserPasswordController appUserPasswordController;

  @BeforeEach
  void beforeEach() {
    appUserPasswordController =
        new AppUserPasswordController(appUserPasswordService, appUserPasswordResetService);
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

  @Test
  @DisplayName("Should redirect if authenticated when get reset password")
  void test6() {
    // Given
    var code = UUID.randomUUID();
    var model = mock(Model.class);
    var authentication = mock(Authentication.class);

    // When
    var res = appUserPasswordController.resetPassword(code, model, authentication);

    // Then
    assertEquals(REDIRECT, res);
  }

  @Test
  @DisplayName(
      "Should show error when get reset password and valid app user password reset is not found")
  void test7() {
    // Given
    var code = UUID.randomUUID();
    var model = new ExtendedModelMap();
    Authentication authentication = null;
    given(appUserPasswordResetService.findStillValidAppUserPasswordReset(code))
        .willReturn(Optional.empty());

    // When
    var res = appUserPasswordController.resetPassword(code, model, authentication);

    // Then
    verify(appUserPasswordResetService, times(1)).findStillValidAppUserPasswordReset(code);
    String passwordResetCodeFailure =
        (String) model.getAttribute(Constants.PASSWORD_RESET_CODE_FAILURE);
    assertNotNull(passwordResetCodeFailure);
    assertEquals("Invalid password reset code", passwordResetCodeFailure);
    PasswordResetDto passwordResetDto = (PasswordResetDto) model.getAttribute("passwordResetDto");
    assertNotNull(passwordResetDto);
    assertNull(passwordResetDto.getPasswordResetCode());
    assertEquals(Constants.PASSWORD_RESET_VIEW, res);
  }

  @Test
  @DisplayName(
      "Should set password reset code when get reset password and valid app user password reset is found")
  void test8() {
    // Given
    var code = UUID.randomUUID();
    var model = new ExtendedModelMap();
    Authentication authentication = null;
    given(appUserPasswordResetService.findStillValidAppUserPasswordReset(code))
        .willReturn(
            Optional.of(
                new AppUserPasswordResetDto(
                    AppUserPasswordReset.builder()
                        .passwordResetCode(code)
                        .appUser(
                            AppUser.builder()
                                .email("appUser@gmail.com")
                                .firstName("First name")
                                .build())
                        .build())));

    // When
    var res = appUserPasswordController.resetPassword(code, model, authentication);

    // Then
    verify(appUserPasswordResetService, times(1)).findStillValidAppUserPasswordReset(code);
    String passwordResetCodeFailure =
        (String) model.getAttribute(Constants.PASSWORD_RESET_CODE_FAILURE);
    assertNull(passwordResetCodeFailure);
    PasswordResetDto passwordResetDto = (PasswordResetDto) model.getAttribute("passwordResetDto");
    assertNotNull(passwordResetDto);
    assertEquals(code, passwordResetDto.getPasswordResetCode());
    assertEquals(Constants.PASSWORD_RESET_VIEW, res);
  }
}
