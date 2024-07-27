package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.appuserpasswords.AppUserPasswordResetTestUtils.createAppUserPasswordReset;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.config.Constants;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;

class AppUserPasswordModificationServiceTests extends DailyAbstractUnitTests {

  private static final AppUser APP_USER = createAppUser(UUID.randomUUID(), "appuser@email.com");
  @Mock private AppUserPasswordResetRepository appUserPasswordResetRepository;
  @Mock private AppUserRepository appUserRepository;
  @Mock private CompromisedPasswordChecker compromisedPasswordChecker;
  @Mock private PasswordEncoder passwordEncoder;
  private AppUserPasswordModificationService appUserPasswordModificationService;

  private static Stream<Arguments> test12() {
    // PasswordChangeDto, appUser
    var passwordChangeDto = new PasswordChangeDto("oldPassword", "newPassword", "newPassword");
    return Stream.of(
        arguments(null, null), arguments(null, APP_USER), arguments(passwordChangeDto, null));
  }

  @BeforeEach
  void beforeEach() {
    appUserPasswordModificationService =
        new AppUserPasswordModificationService(
            appUserPasswordResetRepository,
            appUserRepository,
            compromisedPasswordChecker,
            passwordEncoder);
  }

  @Test
  @DisplayName("Should throw when create app user password reset with null e-mail")
  void test1() {
    // Given
    String email = null;

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () -> appUserPasswordModificationService.createAppUserPasswordReset(email));

    // Then
    assertNotNull(res);
  }

  @Test
  @DisplayName("Should return empty when create app user password reset and AppUser does not exist")
  void test2() {
    // Given
    var email = "appUser@gmail.com";
    var authProvider = AuthProvider.DAILY;
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProviderAndEnabledTrue(email, authProvider))
        .willReturn(Optional.empty());

    // When
    var res = appUserPasswordModificationService.createAppUserPasswordReset(email);

    // Then
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName(
      "Should return empty when create app user password reset and AppUser exists but there is already another AppUserPasswordReset for that")
  void test3() {
    // Given
    var email = "appUser@gmail.com";
    var authProvider = AuthProvider.DAILY;
    var appUser = createAppUser(UUID.randomUUID(), email, authProvider, "authProviderId");
    var passwordResetCode = UUID.randomUUID();
    var expiredAt =
        LocalDateTime.now().plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES);
    var appUserPasswordReset = createAppUserPasswordReset(passwordResetCode, expiredAt, appUser);
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProviderAndEnabledTrue(email, authProvider))
        .willReturn(Optional.of(appUser));
    given(appUserPasswordResetRepository.findByAppUser(appUser))
        .willReturn(Optional.of(appUserPasswordReset));

    // When
    var res = appUserPasswordModificationService.createAppUserPasswordReset(email);

    // Then
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName(
      "Should return AppUserPasswordResetDto when create app user password reset and AppUser exists, there is no other AppUserPasswordReset for that and it can be saved")
  void test4() {
    // Given
    var email = "appUser@gmail.com";
    var authProvider = AuthProvider.DAILY;
    var appUser = createAppUser(UUID.randomUUID(), email, authProvider, "authProviderId");
    var firstName = "First name";
    appUser.setFirstName(firstName);
    var passwordResetCode = UUID.randomUUID();
    var expiredAt =
        LocalDateTime.now().plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES);
    var appUserPasswordReset = createAppUserPasswordReset(passwordResetCode, expiredAt, appUser);
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProviderAndEnabledTrue(email, authProvider))
        .willReturn(Optional.of(appUser));
    given(appUserPasswordResetRepository.findByAppUser(appUser)).willReturn(Optional.empty());
    given(appUserPasswordResetRepository.save(any())).willReturn(appUserPasswordReset);

    // When
    var res = appUserPasswordModificationService.createAppUserPasswordReset(email);

    // Then
    assertTrue(res.isPresent());
    var appUserPasswordResetDto = res.get();
    assertEquals(StringUtils.toRootLowerCase(email), appUserPasswordResetDto.getAppUserEmail());
    assertEquals(passwordResetCode, appUserPasswordResetDto.getPasswordResetCode());
    assertEquals(expiredAt, appUserPasswordResetDto.getExpiredAt());
    assertEquals(firstName, appUserPasswordResetDto.getAppUserFirstName());
  }

  @Test
  @DisplayName(
      "Should return empty value when find still valid app user password reset is not found")
  void test5() {
    // Given
    var code = UUID.randomUUID();
    given(
            appUserPasswordResetRepository.findStillValidAppUserPasswordResetFetchEnabledAppUser(
                eq(code), any()))
        .willReturn(Optional.empty());

    // When
    var res = appUserPasswordModificationService.findStillValidAppUserPasswordReset(code);

    // Then
    verify(appUserPasswordResetRepository, times(1))
        .findStillValidAppUserPasswordResetFetchEnabledAppUser(eq(code), any());
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName(
      "Should return app user password reset dto when find still valid app user password reset is found")
  void test6() {
    // Given
    var code = UUID.randomUUID();
    var appUserPasswordReset =
        AppUserPasswordReset.builder()
            .passwordResetCode(code)
            .usedAt(null)
            .expiredAt(
                LocalDateTime.now()
                    .plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES))
            .appUser(
                AppUser.builder()
                    .enabled(true)
                    .email("appUser@gmail.com")
                    .firstName("First name")
                    .build())
            .build();
    given(
            appUserPasswordResetRepository.findStillValidAppUserPasswordResetFetchEnabledAppUser(
                eq(code), any()))
        .willReturn(Optional.of(appUserPasswordReset));

    // When
    var res = appUserPasswordModificationService.findStillValidAppUserPasswordReset(code);

    // Then
    verify(appUserPasswordResetRepository, times(1))
        .findStillValidAppUserPasswordResetFetchEnabledAppUser(eq(code), any());
    assertTrue(res.isPresent());
    var appUserPasswordResetDto = res.get();
    assertEquals(code, appUserPasswordResetDto.getPasswordResetCode());
    assertEquals(appUserPasswordReset.getExpiredAt(), appUserPasswordResetDto.getExpiredAt());
    assertEquals(
        appUserPasswordReset.getAppUser().getEmail(), appUserPasswordResetDto.getAppUserEmail());
    assertEquals(
        appUserPasswordReset.getAppUser().getFirstName(),
        appUserPasswordResetDto.getAppUserFirstName());
  }

  @Test
  @DisplayName("Should throw when reset app user password with null argument")
  void test7() {
    // Given
    PasswordResetDto passwordResetDto = null;

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () -> appUserPasswordModificationService.resetAppUserPassword(passwordResetDto));

    // Then
    assertNotNull(res);
  }

  @Test
  @DisplayName(
      "Should throw when reset app user password and valid app user password reset does not exist")
  void test8() {
    // Given
    var passwordResetDto = new PasswordResetDto();
    passwordResetDto.setPassword("newPassword");
    passwordResetDto.setPasswordConfirmation("newPassword");
    var passwordResetCode = UUID.randomUUID();
    passwordResetDto.setPasswordResetCode(passwordResetCode);
    given(
            appUserPasswordResetRepository.findStillValidAppUserPasswordResetFetchEnabledAppUser(
                eq(passwordResetCode), any()))
        .willReturn(Optional.empty());

    // When
    var res =
        assertThrows(
            NoSuchElementException.class,
            () -> appUserPasswordModificationService.resetAppUserPassword(passwordResetDto));

    // Then
    assertNotNull(res);
  }

  @Test
  @DisplayName("Should throw when reset app user password and new password is compromised")
  void test9() {
    // Given
    var passwordResetDto = new PasswordResetDto();
    passwordResetDto.setPassword("newPassword");
    passwordResetDto.setPasswordConfirmation("newPassword");
    var passwordResetCode = UUID.randomUUID();
    passwordResetDto.setPasswordResetCode(passwordResetCode);
    given(
            appUserPasswordResetRepository.findStillValidAppUserPasswordResetFetchEnabledAppUser(
                eq(passwordResetCode), any()))
        .willReturn(
            Optional.of(
                AppUserPasswordReset.builder()
                    .passwordResetCode(passwordResetCode)
                    .appUser(
                        AppUser.builder()
                            .enabled(true)
                            .authProvider(AuthProvider.DAILY)
                            .email("appuser@gmail.com")
                            .firstName("FirstName")
                            .build())
                    .usedAt(null)
                    .expiredAt(
                        LocalDateTime.now()
                            .plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES))
                    .build()));
    given(compromisedPasswordChecker.check(passwordResetDto.getPassword()))
        .willReturn(new CompromisedPasswordDecision(true));

    // When
    var res =
        assertThrows(
            CompromisedPasswordException.class,
            () -> appUserPasswordModificationService.resetAppUserPassword(passwordResetDto));

    // Then
    assertNotNull(res);
  }

  @Test
  @DisplayName(
      "Should return app user password reset dto when reset app user password and new password is correctly set")
  void test10() {
    // Given
    var passwordResetDto = new PasswordResetDto();
    var newPassword = "nonCompromisedNewPassword";
    passwordResetDto.setPassword(newPassword);
    passwordResetDto.setPasswordConfirmation(newPassword);
    var passwordResetCode = UUID.randomUUID();
    var encodedNewPassword = "encodedNewPassword";
    var firstName = "FirstName";
    var email = "appuser@gmail.com";
    var expiredAt =
        LocalDateTime.now().plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES);
    passwordResetDto.setPasswordResetCode(passwordResetCode);
    given(
            appUserPasswordResetRepository.findStillValidAppUserPasswordResetFetchEnabledAppUser(
                eq(passwordResetCode), any()))
        .willReturn(
            Optional.of(
                AppUserPasswordReset.builder()
                    .passwordResetCode(passwordResetCode)
                    .appUser(
                        AppUser.builder()
                            .enabled(true)
                            .authProvider(AuthProvider.DAILY)
                            .email(email)
                            .firstName(firstName)
                            .build())
                    .usedAt(null)
                    .expiredAt(expiredAt)
                    .build()));
    given(compromisedPasswordChecker.check(passwordResetDto.getPassword()))
        .willReturn(new CompromisedPasswordDecision(false));
    given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

    // When
    var res = appUserPasswordModificationService.resetAppUserPassword(passwordResetDto);

    // Then
    assertTrue(res.isPresent());
    var appUserPasswordResetDto = res.get();
    assertEquals(email, appUserPasswordResetDto.getAppUserEmail());
    assertEquals(firstName, appUserPasswordResetDto.getAppUserFirstName());
    assertEquals(passwordResetCode, appUserPasswordResetDto.getPasswordResetCode());
    assertEquals(expiredAt, appUserPasswordResetDto.getExpiredAt());
    assertEquals(encodedNewPassword, appUserPasswordResetDto.getAppUserEncodedPassword());
  }

  @Test
  @DisplayName("Should throw when find still valid app user password reset with null argument")
  void test11() {
    // Given
    UUID passwordResetCode = null;

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                appUserPasswordModificationService.findStillValidAppUserPasswordReset(
                    passwordResetCode));

    // Then
    assertNotNull(res);
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when change app user password with null argument")
  void test12(PasswordChangeDto passwordChangeDto, AppUser appUser) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserPasswordModificationService.changeAppUserPassword(passwordChangeDto, appUser));
  }
}
