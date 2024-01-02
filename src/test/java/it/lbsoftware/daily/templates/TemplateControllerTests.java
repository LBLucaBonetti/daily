package it.lbsoftware.daily.templates;

import static it.lbsoftware.daily.config.Constants.LOGIN_VIEW;
import static it.lbsoftware.daily.config.Constants.SIGNUP_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.appusers.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

class TemplateControllerTests extends DailyAbstractUnitTests {

  private static final String REDIRECT = "redirect:/";
  @Mock private AppUserService appUserService;
  private TemplateController templateController;

  @BeforeEach
  void beforeEach() {
    templateController = new TemplateController(appUserService);
  }

  @Test
  @DisplayName("Should redirect if authenticated when get signup")
  void test1() {
    // Given
    Model model = mock(Model.class);
    Authentication authentication = mock(Authentication.class);

    // When
    var res = templateController.signup(model, authentication);

    // Then
    assertEquals(REDIRECT, res);
  }

  @Test
  @DisplayName("Should redirect if authenticated when get login")
  void test2() {
    // Given
    Authentication authentication = mock(Authentication.class);

    // When
    var res = templateController.login(authentication);

    // Then
    assertEquals(REDIRECT, res);
  }

  @Test
  @DisplayName("Should redirect if authenticated when post signup")
  void test3() {
    // Given
    AppUserDto appUserDto = mock(AppUserDto.class);
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    Authentication authentication = mock(Authentication.class);

    // When
    var res = templateController.signup(appUserDto, bindingResult, model, authentication);

    // Then
    assertEquals(REDIRECT, res);
  }

  @Test
  @DisplayName("Should return signup when get signup")
  void test4() {
    // Given
    Model model = mock(Model.class);
    Authentication authentication = null;

    // When
    var res = templateController.signup(model, authentication);

    // Then
    assertEquals(SIGNUP_VIEW, res);
  }

  @Test
  @DisplayName("Should return login when get login")
  void test5() {
    // Given
    Authentication authentication = null;

    // When
    var res = templateController.login(authentication);

    // Then
    assertEquals(LOGIN_VIEW, res);
  }

  @Test
  @DisplayName("Should call signup service when post signup")
  void test6() {
    // Given
    AppUserDto appUserDto = mock(AppUserDto.class);
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    Authentication authentication = null;

    // When
    var res = templateController.signup(appUserDto, bindingResult, model, authentication);

    // Then
    verify(appUserService, times(1)).signup(appUserDto, bindingResult, model);
  }
}
