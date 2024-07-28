package it.lbsoftware.daily.appuserpasswords;

import static org.junit.jupiter.api.Assertions.assertThrows;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;

class AppUserPasswordSecurityServiceTests extends DailyAbstractUnitTests {

  @Mock private CompromisedPasswordChecker compromisedPasswordChecker;
  private AppUserPasswordSecurityService appUserPasswordSecurityService;

  @BeforeEach
  void beforeEach() {
    appUserPasswordSecurityService = new AppUserPasswordSecurityService(compromisedPasswordChecker);
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
}
