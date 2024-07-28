package it.lbsoftware.daily.appuseractivations;

import static it.lbsoftware.daily.appuseractivations.AppUserActivationTestUtils.createAppUserActivation;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static it.lbsoftware.daily.config.Constants.ACTIVATIONS_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.config.DailyConfig;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;

class AppUserActivationServiceImplTests extends DailyAbstractUnitTests {

  @Mock private AppUserActivationRepository appUserActivationRepository;
  @Mock private DailyConfig dailyConfig;
  private AppUserActivationServiceImpl appUserActivationService;

  @BeforeEach
  void beforeEach() {
    appUserActivationService =
        new AppUserActivationServiceImpl(appUserActivationRepository, dailyConfig);
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when get activation uri with invalid activation code")
  void test1(final UUID activationCode) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserActivationService.getActivationUri(activationCode));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName(
      "Should throw when setNonActivatedAndStillValidAppUserActivationActivated with null activation code")
  void test2(final UUID activationCode) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            appUserActivationService.setNonActivatedAndStillValidAppUserActivationActivated(
                activationCode));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when createAppUserActivation with null app user")
  void test3(final AppUser appUser) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserActivationService.createAppUserActivation(appUser));
  }

  @Test
  @DisplayName("Should get activation uri")
  void test4() {
    // Given
    var baseUri = "http://localhost:8080";
    var activationCode = UUID.randomUUID();
    given(dailyConfig.getBaseUri()).willReturn(baseUri);

    // When
    var res = appUserActivationService.getActivationUri(activationCode);

    // Then
    assertEquals(baseUri + "/" + ACTIVATIONS_VIEW + "/" + activationCode, res);
  }

  @Test
  @DisplayName("Should not create app user activation and return empty optional")
  void test5() {
    // Given
    var appUser =
        createAppUser(
            UUID.randomUUID(),
            "appuser@gmail.com",
            AuthProvider.GOOGLE,
            UUID.randomUUID().toString());

    // When
    var res = appUserActivationService.createAppUserActivation(appUser);

    // Then
    assertTrue(res.isEmpty());
  }

  @Test
  @DisplayName("Should create app user activation and return app user activation optional")
  void test6() {
    // Given
    var appUser =
        createAppUser(
            UUID.randomUUID(),
            "appuser@email.com",
            AuthProvider.DAILY,
            UUID.randomUUID().toString());
    var appUserActivation = new AppUserActivation();
    given(appUserActivationRepository.saveAndFlush(any())).willReturn(appUserActivation);

    // When
    var res = appUserActivationService.createAppUserActivation(appUser);

    // Then
    assertTrue(res.isPresent());
  }

  @Test
  @DisplayName(
      "Should not setNonActivatedAndStillValidAppUserActivationActivated because of appUserActivation not found and return false")
  void test7() {
    // Given
    var activationCode = UUID.randomUUID();
    given(
            appUserActivationRepository.findNonActivatedAndStillValidAppUserActivationFetchAppUser(
                any(), any()))
        .willReturn(Optional.empty());

    // When
    var res =
        appUserActivationService.setNonActivatedAndStillValidAppUserActivationActivated(
            activationCode);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should setNonActivatedAndStillValidAppUserActivationActivated and return true")
  void test8() {
    // Given
    var activationCode = UUID.randomUUID();
    var appUserActivation =
        createAppUserActivation(
            activationCode, createAppUser(UUID.randomUUID(), "appuser@email.com"));
    given(
            appUserActivationRepository.findNonActivatedAndStillValidAppUserActivationFetchAppUser(
                any(), any()))
        .willReturn(Optional.of(appUserActivation));

    // When
    var res =
        appUserActivationService.setNonActivatedAndStillValidAppUserActivationActivated(
            activationCode);

    // Then
    assertTrue(res);
  }
}
