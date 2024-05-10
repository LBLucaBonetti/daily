package it.lbsoftware.daily.appusersignups;

import static it.lbsoftware.daily.config.Constants.ACTIVATIONS_VIEW;
import static it.lbsoftware.daily.config.Constants.SIGNUP_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appusercreations.AppUserCreationService;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.emails.EmailService;
import it.lbsoftware.daily.exceptions.DailyEmailException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

class AppUserSignupServiceImplTests extends DailyAbstractUnitTests {

  @Mock private AppUserCreationService appUserCreationService;
  @Mock private EmailService emailService;
  @Mock private AppUserActivationService appUserActivationService;
  private AppUserSignupServiceImpl appUserSignupService;

  private static Stream<Arguments> test1() {
    // AppUsedDto, bindingResult, model
    AppUserDto appUserDto = new AppUserDto();
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    return Stream.of(
        arguments(null, null, null),
        arguments(null, null, model),
        arguments(null, bindingResult, null),
        arguments(null, bindingResult, model),
        arguments(appUserDto, null, null),
        arguments(appUserDto, null, model),
        arguments(appUserDto, bindingResult, null));
  }

  @BeforeEach
  void beforeEach() {
    appUserSignupService =
        new AppUserSignupServiceImpl(
            appUserCreationService, emailService, appUserActivationService);
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when signup with null argument")
  void test1(AppUserDto appUserDto, BindingResult bindingResult, Model model) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserSignupService.signup(appUserDto, bindingResult, model));
  }

  @Test
  @DisplayName("Should return early when binding result has errors")
  void test2() {
    // Given
    var appUserDto = mock(AppUserDto.class);
    var bindingResult = mock(BindingResult.class);
    var model = mock(Model.class);
    when(bindingResult.hasErrors()).thenReturn(true);

    // When
    appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    verify(bindingResult, times(1)).hasErrors();
    verify(appUserDto, times(0)).getPassword();
    verify(appUserDto, times(0)).getPasswordConfirmation();
  }

  @Test
  @DisplayName("Should return early when passwords do not match")
  void test3() {
    // Given
    var appUserDto = mock(AppUserDto.class);
    var bindingResult = mock(BindingResult.class);
    var model = mock(Model.class);
    when(appUserDto.getPassword()).thenReturn("password1");
    when(appUserDto.getPasswordConfirmation()).thenReturn("password2");
    when(bindingResult.hasErrors()).thenReturn(false);

    // When
    appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    verify(appUserDto, times(1)).getPassword();
    verify(appUserDto, times(1)).getPasswordConfirmation();
    verify(appUserDto, times(0)).getEmail();
  }

  @Test
  @DisplayName("Should return early when e-mail is from an OAuth2 provider")
  void test4() {
    // Given
    var appUserDto = mock(AppUserDto.class);
    var bindingResult = mock(BindingResult.class);
    var model = mock(Model.class);
    when(bindingResult.hasErrors()).thenReturn(false);
    when(appUserDto.getPassword()).thenReturn("password");
    when(appUserDto.getPasswordConfirmation()).thenReturn("password");
    when(appUserDto.getEmail()).thenReturn("appuser@gmail.com");

    // When
    appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    verify(appUserDto, times(1)).getEmail();
    verify(appUserCreationService, times(0)).createDailyAppUser(appUserDto);
  }

  @Test
  @DisplayName("Should add error to view when daily app user creation fails")
  void test5() {
    // Given
    var appUserDto = mock(AppUserDto.class);
    var bindingResult = mock(BindingResult.class);
    var model = mock(Model.class);
    when(bindingResult.hasErrors()).thenReturn(false);
    when(appUserDto.getPassword()).thenReturn("password");
    when(appUserDto.getPasswordConfirmation()).thenReturn("password");
    when(appUserDto.getEmail()).thenReturn("appuser@email.com");
    when(appUserCreationService.createDailyAppUser(appUserDto)).thenReturn(Optional.empty());

    // When
    appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    verify(appUserCreationService, times(1)).createDailyAppUser(appUserDto);
    verify(bindingResult, times(1)).addError(any());
    verify(model, times(0)).addAttribute(eq(SIGNUP_SUCCESS), anyString());
    verify(appUserActivationService, times(0)).getActivationUri(any());
    verify(emailService, times(0)).sendSynchronously(any(), any());
  }

  @Test
  @DisplayName("Should send activation e-mail when daily app user creation succeeds")
  void test6() {
    // Given
    var appUserDto = mock(AppUserDto.class);
    var bindingResult = mock(BindingResult.class);
    var model = mock(Model.class);
    when(bindingResult.hasErrors()).thenReturn(false);
    when(appUserDto.getPassword()).thenReturn("password");
    when(appUserDto.getPasswordConfirmation()).thenReturn("password");
    when(appUserDto.getEmail()).thenReturn("appuser@email.com");
    when(appUserDto.getFirstName()).thenReturn("First name");
    var activationCode = UUID.randomUUID();
    when(appUserCreationService.createDailyAppUser(appUserDto))
        .thenReturn(Optional.of(activationCode));
    var activationUri = "http://localhost/daily/" + ACTIVATIONS_VIEW + "/" + activationCode;
    when(appUserActivationService.getActivationUri(activationCode)).thenReturn(activationUri);

    // When
    appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    verify(appUserCreationService, times(1)).createDailyAppUser(appUserDto);
    verify(bindingResult, times(0)).addError(any());
    verify(model, times(1)).addAttribute(eq(SIGNUP_SUCCESS), anyString());
    verify(appUserActivationService, times(1)).getActivationUri(activationCode);
    verify(emailService, times(1)).sendSynchronously(any(), any());
  }

  @Test
  @DisplayName(
      "Should add error to view when daily app user creation succeeds but activation email is not sent")
  void test7() {
    // Given
    var appUserDto = mock(AppUserDto.class);
    var bindingResult = mock(BindingResult.class);
    var model = mock(Model.class);
    var activationCode = UUID.randomUUID();
    when(bindingResult.hasErrors()).thenReturn(false);
    when(appUserDto.getPassword()).thenReturn("password");
    when(appUserDto.getPasswordConfirmation()).thenReturn("password");
    when(appUserDto.getEmail()).thenReturn("appuser@email.com");
    when(appUserDto.getFirstName()).thenReturn("First name");
    when(appUserCreationService.createDailyAppUser(appUserDto))
        .thenReturn(Optional.of(activationCode));
    var activationUri = "http://localhost/daily/" + ACTIVATIONS_VIEW + "/" + activationCode;
    when(appUserActivationService.getActivationUri(activationCode)).thenReturn(activationUri);
    doThrow(new DailyEmailException()).when(emailService).sendSynchronously(any(), any());

    // When
    appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    verify(appUserCreationService, times(1)).createDailyAppUser(appUserDto);
    verify(bindingResult, times(1)).addError(any());
    verify(model, times(0)).addAttribute(eq(SIGNUP_SUCCESS), anyString());
    verify(appUserActivationService, times(1)).getActivationUri(activationCode);
    verify(emailService, times(1)).sendSynchronously(any(), any());
  }
}
