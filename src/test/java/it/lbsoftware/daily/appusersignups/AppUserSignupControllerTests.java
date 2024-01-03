package it.lbsoftware.daily.appusersignups;

import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static it.lbsoftware.daily.config.Constants.SIGNUP_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

class AppUserSignupControllerTests extends DailyAbstractUnitTests {

  @Mock private AppUserSignupServiceImpl appUserSignupService;
  private AppUserSignupController appUserSignupController;

  @BeforeEach
  void beforeEach() {
    appUserSignupController = new AppUserSignupController(appUserSignupService);
  }

  @Test
  @DisplayName("Should redirect if authenticated when get signup")
  void test1() {
    // Given
    Model model = mock(Model.class);
    Authentication authentication = mock(Authentication.class);

    // When
    var res = appUserSignupController.signup(model, authentication);

    // Then
    assertEquals(REDIRECT, res);
  }

  @Test
  @DisplayName("Should redirect if authenticated when post signup")
  void test2() {
    // Given
    AppUserDto appUserDto = mock(AppUserDto.class);
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    Authentication authentication = mock(Authentication.class);

    // When
    var res = appUserSignupController.signup(appUserDto, bindingResult, model, authentication);

    // Then
    assertEquals(REDIRECT, res);
    verify(appUserSignupService, times(0)).signup(any(), any(), any());
  }

  @Test
  @DisplayName("Should return signup when get signup")
  void test3() {
    // Given
    Model model = mock(Model.class);
    Authentication authentication = null;

    // When
    var res = appUserSignupController.signup(model, authentication);

    // Then
    assertEquals(SIGNUP_VIEW, res);
  }

  @Test
  @DisplayName("Should call signup service when post signup")
  void test4() {
    // Given
    AppUserDto appUserDto = mock(AppUserDto.class);
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    Authentication authentication = null;

    // When
    appUserSignupController.signup(appUserDto, bindingResult, model, authentication);

    // Then
    verify(appUserSignupService, times(1)).signup(appUserDto, bindingResult, model);
  }
}
