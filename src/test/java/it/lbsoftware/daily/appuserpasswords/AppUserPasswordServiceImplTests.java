package it.lbsoftware.daily.appuserpasswords;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserTestUtils;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.config.DailyConfig;
import it.lbsoftware.daily.emails.EmailService;
import it.lbsoftware.daily.exceptions.DailyEmailException;
import java.time.LocalDateTime;
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

  @Mock private AppUserPasswordResetRepository appUserPasswordResetRepository;
  @Mock private AppUserRepository appUserRepository;
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
        new AppUserPasswordServiceImpl(
            appUserPasswordResetRepository, appUserRepository, emailService, dailyConfig);
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
  @DisplayName("Should show success if app user is not found")
  void test2() {
    // Given
    var email = "appuser@email.com";
    var passwordResetNotificationDto = new PasswordResetNotificationDto(email);
    var model = new ExtendedModelMap();
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProvider(email, AuthProvider.DAILY))
        .willReturn(Optional.empty());

    // When
    appUserPasswordService.sendPasswordResetNotification(passwordResetNotificationDto, model);

    // Then
    verify(appUserPasswordResetRepository, times(0)).findByAppUser(any());
    assertEquals(
        Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS_MESSAGE,
        model.getAttribute(Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS));
  }

  @Test
  @DisplayName(
      "Should show success if app user is found and app user password reset could not be created")
  void test3() {
    // Given
    var email = "appuser@email.com";
    var appUser =
        AppUserTestUtils.createAppUser(
            UUID.randomUUID(), email, AuthProvider.DAILY, "authProviderId");
    var passwordResetNotificationDto = new PasswordResetNotificationDto(email);
    var model = new ExtendedModelMap();
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProvider(email, AuthProvider.DAILY))
        .willReturn(Optional.of(appUser));
    doThrow(new RuntimeException()).when(appUserPasswordResetRepository).save(any());

    // When
    appUserPasswordService.sendPasswordResetNotification(passwordResetNotificationDto, model);

    // Then
    verify(appUserPasswordResetRepository, times(1)).save(any());
    verify(emailService, times(0)).sendSynchronously(any(), any());
    assertEquals(
        Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS_MESSAGE,
        model.getAttribute(Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS));
  }

  @Test
  @DisplayName(
      "Should throw if app user is found, app user password reset is created and send password reset notification throws")
  void test4() {
    // Given
    var email = "appuser@email.com";
    var appUser =
        AppUserTestUtils.createAppUser(
            UUID.randomUUID(), email, AuthProvider.DAILY, "authProviderId");
    var passwordResetNotificationDto = new PasswordResetNotificationDto(email);
    var model = new ExtendedModelMap();
    var appUserPasswordReset =
        AppUserPasswordReset.builder()
            .appUser(appUser)
            .passwordResetCode(UUID.randomUUID())
            .expiredAt(
                LocalDateTime.now()
                    .plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES))
            .build();
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProvider(email, AuthProvider.DAILY))
        .willReturn(Optional.of(appUser));
    given(appUserPasswordResetRepository.save(any())).willReturn(appUserPasswordReset);
    given(dailyConfig.getBaseUri()).willReturn("http://localhost:8080");
    doThrow(new DailyEmailException()).when(emailService).sendSynchronously(any(), any());

    // When
    var res =
        assertThrows(
            DailyEmailException.class,
            () ->
                appUserPasswordService.sendPasswordResetNotification(
                    passwordResetNotificationDto, model));

    // Then
    assertNull(model.getAttribute(Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS));
    assertNotNull(res);
  }

  @Test
  @DisplayName("Should throw if app user password reset code is null")
  void test5() {
    // Given
    var email = "appuser@email.com";
    var appUser =
        AppUserTestUtils.createAppUser(
            UUID.randomUUID(), email, AuthProvider.DAILY, "authProviderId");
    var passwordResetNotificationDto = new PasswordResetNotificationDto(email);
    var model = new ExtendedModelMap();
    var appUserPasswordReset =
        AppUserPasswordReset.builder()
            .appUser(appUser)
            .passwordResetCode(null)
            .expiredAt(
                LocalDateTime.now()
                    .plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES))
            .build();
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProvider(email, AuthProvider.DAILY))
        .willReturn(Optional.of(appUser));
    given(appUserPasswordResetRepository.save(any())).willReturn(appUserPasswordReset);

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                appUserPasswordService.sendPasswordResetNotification(
                    passwordResetNotificationDto, model));

    // Then
    assertNull(model.getAttribute(Constants.PASSWORD_RESET_NOTIFICATION_SUCCESS));
    assertNotNull(res);
  }
}
