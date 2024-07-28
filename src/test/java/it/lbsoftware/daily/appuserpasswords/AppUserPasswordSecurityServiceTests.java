package it.lbsoftware.daily.appuserpasswords;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.exceptions.DailyNotEnoughSecurePasswordException;
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.scoring.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.authentication.password.CompromisedPasswordException;

class AppUserPasswordSecurityServiceTests extends DailyAbstractUnitTests {

  @Mock private CompromisedPasswordChecker compromisedPasswordChecker;
  @Mock private Nbvcxz nbvcxz;
  private AppUserPasswordSecurityService appUserPasswordSecurityService;

  @BeforeEach
  void beforeEach() {
    appUserPasswordSecurityService =
        new AppUserPasswordSecurityService(compromisedPasswordChecker, nbvcxz);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  @DisplayName("Should throw when check with null argument")
  void test1(final String cleartextPassword) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserPasswordSecurityService.check(cleartextPassword));
  }

  @Test
  @DisplayName(
      "Should throw DailyNotEnoughSecurePasswordException when check and password is not enough secure")
  void test2() {
    // Given
    var password = "weakPassword";
    var passwordEstimationResult = mock(Result.class);
    given(nbvcxz.estimate(password)).willReturn(passwordEstimationResult);
    given(passwordEstimationResult.isMinimumEntropyMet()).willReturn(false);

    // When
    var res =
        assertThrows(
            DailyNotEnoughSecurePasswordException.class,
            () -> appUserPasswordSecurityService.check(password));

    // Then
    assertNotNull(res);
  }

  @Test
  @DisplayName("Should throw CompromisedPasswordException when check and password is compromised")
  void test3() {
    // Given
    var password = "Th1sPazzwordIsHardEnoughBut1sCompromised!";
    var passwordEstimationResult = mock(Result.class);
    given(nbvcxz.estimate(password)).willReturn(passwordEstimationResult);
    given(passwordEstimationResult.isMinimumEntropyMet()).willReturn(true);
    var compromisedResult = mock(CompromisedPasswordDecision.class);
    given(compromisedPasswordChecker.check(password)).willReturn(compromisedResult);
    given(compromisedResult.isCompromised()).willReturn(true);

    // When
    var res =
        assertThrows(
            CompromisedPasswordException.class,
            () -> appUserPasswordSecurityService.check(password));

    // Then
    assertNotNull(res);
  }
}
