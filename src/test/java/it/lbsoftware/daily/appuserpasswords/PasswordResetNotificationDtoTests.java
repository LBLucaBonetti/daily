package it.lbsoftware.daily.appuserpasswords;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordResetNotificationDtoTests extends DailyAbstractUnitTests {

  @Test
  @DisplayName("Should always create instance with lowercase e-mail")
  void test1() {
    // Given
    var email = "APPUSER@GMAIL.COM";

    // When
    var res = new PasswordResetNotificationDto(email);

    // Then
    assertEquals("appuser@gmail.com", res.getEmail());
  }
}
