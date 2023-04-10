package it.lbsoftware.daily.appusers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@DisplayName("AppUserDetailsService unit tests")
class AppUserDetailsServiceTests extends DailyAbstractUnitTests {

  @Mock private AppUserRepository appUserRepository;
  private AppUserDetailsService appUserDetailsService;

  @BeforeEach
  void beforeEach() {
    appUserDetailsService = new AppUserDetailsService(appUserRepository);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should throw when load user by username with invalid username")
  void test1(String username) {
    // When
    var res =
        assertThrows(
            UsernameNotFoundException.class,
            () -> appUserDetailsService.loadUserByUsername(username));

    // Then
    assertNotNull(res.getMessage());
  }

  @Test
  @DisplayName("Should throw when load user by username and app user is not found")
  void test2() {
    // Given
    String email = "appuser@email.com";
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProvider(email, AuthProvider.DAILY))
        .willReturn(Optional.empty());

    // When
    var res =
        assertThrows(
            UsernameNotFoundException.class, () -> appUserDetailsService.loadUserByUsername(email));

    // Then
    assertNotNull(res.getMessage());
  }

  @Test
  @DisplayName("Should load user by username and return app user details")
  void test3() {
    // Given
    String email = "appuser@email.com";
    AppUser appUser = AppUser.builder().email(email).build();
    given(appUserRepository.findByEmailIgnoreCaseAndAuthProvider(email, AuthProvider.DAILY))
        .willReturn(Optional.of(appUser));

    // When
    var res = appUserDetailsService.loadUserByUsername(email);

    // Then
    assertEquals(email, res.getUsername());
  }
}
