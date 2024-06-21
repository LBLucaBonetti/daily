package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.appuserpasswords.AppUserPasswordResetTestUtils.createAppUserPasswordReset;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.config.Constants;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class AppUserPasswordResetServiceTests extends DailyAbstractUnitTests {

  @Mock private AppUserPasswordResetRepository appUserPasswordResetRepository;
  @Mock private AppUserRepository appUserRepository;
  private AppUserPasswordResetService appUserPasswordResetService;

  @BeforeEach
  void beforeEach() {
    appUserPasswordResetService =
        new AppUserPasswordResetService(appUserPasswordResetRepository, appUserRepository);
  }

  @Test
  @DisplayName("Should throw with null e-mail")
  void test1() {
    // Given
    String email = null;

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () -> appUserPasswordResetService.createAppUserPasswordReset(email));

    // Then
    assertNotNull(res);
  }

  @Test
  @DisplayName("Should return empty when AppUser does not exist")
  void test2() {
    // Given
    var email = "appUser@gmail.com";
    var authProvider = AuthProvider.DAILY;
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProvider(email, authProvider))
        .willReturn(Optional.empty());

    // When
    var res = appUserPasswordResetService.createAppUserPasswordReset(email);

    // Then
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName(
      "Should return empty when AppUser exists but there is already another AppUserPasswordReset for that")
  void test3() {
    // Given
    var email = "appUser@gmail.com";
    var authProvider = AuthProvider.DAILY;
    var appUser = createAppUser(UUID.randomUUID(), email, authProvider, "authProviderId");
    var passwordResetCode = UUID.randomUUID();
    var expiredAt =
        LocalDateTime.now().plusMinutes(Constants.PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES);
    var appUserPasswordReset = createAppUserPasswordReset(passwordResetCode, expiredAt, appUser);
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProvider(email, authProvider))
        .willReturn(Optional.of(appUser));
    given(appUserPasswordResetRepository.findByAppUser(appUser))
        .willReturn(Optional.of(appUserPasswordReset));

    // When
    var res = appUserPasswordResetService.createAppUserPasswordReset(email);

    // Then
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName(
      "Should return AppUserPasswordResetDto when AppUser exists, there is no other AppUserPasswordReset for that and it can be saved")
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
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProvider(email, authProvider))
        .willReturn(Optional.of(appUser));
    given(appUserPasswordResetRepository.findByAppUser(appUser)).willReturn(Optional.empty());
    given(appUserPasswordResetRepository.save(any())).willReturn(appUserPasswordReset);

    // When
    var res = appUserPasswordResetService.createAppUserPasswordReset(email);

    // Then
    assertTrue(res.isPresent());
    var appUserPasswordResetDto = res.get();
    assertEquals(StringUtils.toRootLowerCase(email), appUserPasswordResetDto.getAppUserEmail());
    assertEquals(passwordResetCode, appUserPasswordResetDto.getPasswordResetCode());
    assertEquals(expiredAt, appUserPasswordResetDto.getExpiredAt());
    assertEquals(firstName, appUserPasswordResetDto.getAppUserFirstName());
  }
}
