package it.lbsoftware.daily.appuserpasswords;

import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static it.lbsoftware.daily.config.Constants.SEND_PASSWORD_RESET_LINK_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

class AppUserPasswordControllerTests extends DailyAbstractUnitTests {

  private AppUserPasswordController appUserPasswordController;

  @BeforeEach
  void beforeEach() {
    appUserPasswordController = new AppUserPasswordController();
  }

  @Test
  @DisplayName("Should redirect if authenticated when get send password reset link")
  void test1() {
    // Given
    var model = mock(Model.class);
    var authentication = mock(Authentication.class);

    // When
    var res = appUserPasswordController.sendPasswordResetLink(model, authentication);

    // Then
    verify(model, times(0)).addAttribute(any(), any());
    assertEquals(REDIRECT, res);
  }

  @Test
  @DisplayName("Should return send password reset link view when get send password reset link")
  void test2() {
    // Given
    var model = mock(Model.class);
    Authentication authentication = null;

    // When
    var res = appUserPasswordController.sendPasswordResetLink(model, authentication);

    // Then
    verify(model, times(1)).addAttribute(any(), any());
    assertEquals(SEND_PASSWORD_RESET_LINK_VIEW, res);
  }
}
