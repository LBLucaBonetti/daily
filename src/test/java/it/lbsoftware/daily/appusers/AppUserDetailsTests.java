package it.lbsoftware.daily.appusers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppUserDetailsTests extends DailyAbstractUnitTests {

  private AppUserDetails appUserDetails;

  @Test
  @DisplayName("Should create instance with app user values")
  void test1() {
    // Given
    String lowerCaseEmail = "appuser@email.com";
    String upperCaseEmail = "APPUSER@EMAIL.COM";
    AppUser appUser =
        new AppUser(
            "authProviderId",
            AuthProvider.DAILY,
            upperCaseEmail,
            "password",
            "FirstName",
            "LastName",
            true,
            null);

    // When
    appUserDetails = new AppUserDetails(appUser);

    // Then
    assertEquals(Collections.emptyList(), appUserDetails.getAuthorities());
    assertEquals(appUser.getPassword(), appUserDetails.getPassword());
    assertEquals(lowerCaseEmail, appUserDetails.getUsername());
    assertTrue(appUserDetails.isAccountNonExpired());
    assertTrue(appUserDetails.isAccountNonLocked());
    assertTrue(appUserDetails.isCredentialsNonExpired());
    assertEquals(appUser.isEnabled(), appUserDetails.isEnabled());
    assertEquals(
        appUser.getFirstName() + " " + appUser.getLastName(), appUserDetails.getFullname());
  }
}
