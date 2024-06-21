package it.lbsoftware.daily.appuserpasswords;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.config.DailyConfig;
import it.lbsoftware.daily.emails.EmailService;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

class AppUserPasswordServiceImplTests extends DailyAbstractUnitTests {

  @Mock private AppUserPasswordResetService appUserPasswordResetService;
  @Mock private EmailService emailService;
  @Mock private DailyConfig dailyConfig;
  private AppUserPasswordServiceImpl appUserPasswordService;

  private static Stream<Arguments> test1() {
    // PasswordResetNotificationDto, model
    var passwordResetNotificationDto = new PasswordResetNotificationDto();
    var model = mock(Model.class);
    return Stream.of(
        arguments(null, null),
        arguments(null, model),
        arguments(passwordResetNotificationDto, null));
  }

  @BeforeEach
  void beforeEach() {
    this.appUserPasswordService =
        new AppUserPasswordServiceImpl(appUserPasswordResetService, emailService, dailyConfig);
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when send password reset notification with null argument")
  void test1(PasswordResetNotificationDto passwordResetNotificationDto, Model model) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            appUserPasswordService.sendPasswordResetNotification(
                passwordResetNotificationDto, model));
  }

  @Test
  @DisplayName(
      "Should show success and not send e-mail if app user password reset could not be created")
  void test2() {
    // Given
    var email = "appuser@email.com";
    var passwordResetNotificationDto = new PasswordResetNotificationDto(email);
    var model = new ExtendedModelMap();
    given(appUserPasswordResetService.createAppUserPasswordReset(email))
        .willReturn(Optional.empty());

    // When
    appUserPasswordService.sendPasswordResetNotification(passwordResetNotificationDto, model);

    // Then
    verify(appUserPasswordResetService, times(1)).createAppUserPasswordReset(email);
    verify(emailService, times(0)).sendAsynchronously(any(), any());
    assertEquals(
        Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS_MESSAGE,
        model.getAttribute(Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS));
  }

  @Test
  @DisplayName("Should show success and send e-mail if app user password reset was created")
  void test3() {
    // Given
    var email = "appuser@email.com";
    var passwordResetNotificationDto = new PasswordResetNotificationDto(email);
    var model = new ExtendedModelMap();
    var appUserPasswordResetDto =
        new AppUserPasswordResetDto(
            AppUserPasswordReset.builder()
                .appUser(AppUser.builder().firstName("First name").email(email).build())
                .passwordResetCode(UUID.randomUUID())
                .build());
    given(appUserPasswordResetService.createAppUserPasswordReset(email))
        .willReturn(Optional.of(appUserPasswordResetDto));
    given(dailyConfig.getBaseUri()).willReturn("http://localhost:8080");

    // When
    appUserPasswordService.sendPasswordResetNotification(passwordResetNotificationDto, model);

    // Then
    verify(appUserPasswordResetService, times(1)).createAppUserPasswordReset(email);
    verify(emailService, times(1)).sendAsynchronously(any(), any());
    assertEquals(
        Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS_MESSAGE,
        model.getAttribute(Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS));
  }

  @Test
  @DisplayName("Should throw and not send e-mail when password reset code is null")
  void test4() {
    // Given
    var email = "appuser@email.com";
    var passwordResetNotificationDto = new PasswordResetNotificationDto(email);
    var model = new ExtendedModelMap();
    var appUserPasswordResetDto =
        new AppUserPasswordResetDto(
            AppUserPasswordReset.builder()
                .appUser(AppUser.builder().firstName("First name").email(email).build())
                .passwordResetCode(null)
                .build());
    given(appUserPasswordResetService.createAppUserPasswordReset(email))
        .willReturn(Optional.of(appUserPasswordResetDto));

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                appUserPasswordService.sendPasswordResetNotification(
                    passwordResetNotificationDto, model));

    // Then
    assertNotNull(res);
    verify(emailService, times(0)).sendAsynchronously(any(), any());
  }
}
