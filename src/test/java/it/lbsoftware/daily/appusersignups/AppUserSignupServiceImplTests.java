package it.lbsoftware.daily.appusersignups;

import static it.lbsoftware.daily.config.Constants.LOGIN_VIEW;
import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static it.lbsoftware.daily.config.Constants.SIGNUP_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusersettings.AppUserSettingDto;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import it.lbsoftware.daily.emails.EmailService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

class AppUserSignupServiceImplTests extends DailyAbstractUnitTests {

  @Mock private AppUserRepository appUserRepository;
  @Mock private AppUserSettingService appUserSettingService;
  @Mock private AppUserActivationService appUserActivationService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private EmailService emailService;
  private AppUserSignupServiceImpl appUserSignupService;

  private static Stream<Arguments> test6() {
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
            appUserRepository,
            appUserSettingService,
            appUserActivationService,
            passwordEncoder,
            emailService);
  }

  @Test
  @DisplayName("Should return signup when binding result has errors")
  void test1() {
    // Given
    AppUserDto appUserDto = mock(AppUserDto.class);
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    given(bindingResult.hasErrors()).willReturn(true);

    // When
    var res = appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    assertEquals(SIGNUP_VIEW, res);
  }

  @Test
  @DisplayName("Should return signup when passwords do not match")
  void test2() {
    // Given
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setPassword("password");
    appUserDto.setPasswordConfirmation("differentPassword");
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    given(bindingResult.hasErrors()).willReturn(false);

    // When
    var res = appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    assertEquals(SIGNUP_VIEW, res);
  }

  @Test
  @DisplayName("Should return signup when email is oauth2")
  void test3() {
    // Given
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setPassword("password");
    appUserDto.setPasswordConfirmation("password");
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    given(bindingResult.hasErrors()).willReturn(false);
    appUserDto.setEmail("email@gmail.com");

    // When
    var res = appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    assertEquals(SIGNUP_VIEW, res);
  }

  @Test
  @DisplayName("Should return signup when email is taken")
  void test4() {
    // Given
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setPassword("password");
    appUserDto.setPasswordConfirmation("password");
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    given(bindingResult.hasErrors()).willReturn(false);
    String email = "email@email.com";
    appUserDto.setEmail(email);
    AppUser appUser = AppUser.builder().email(email).build();
    given(appUserRepository.findByEmailIgnoreCase(email)).willReturn(Optional.of(appUser));

    // When
    var res = appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    assertEquals(SIGNUP_VIEW, res);
  }

  @Test
  @DisplayName("Should return login when signup is performed")
  void test5() {
    // Given
    AppUserDto appUserDto = new AppUserDto();
    String password = "password";
    appUserDto.setPassword(password);
    appUserDto.setPasswordConfirmation(password);
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    given(bindingResult.hasErrors()).willReturn(false);
    String email = "email@email.com";
    appUserDto.setEmail(email);
    given(appUserRepository.findByEmailIgnoreCase(email)).willReturn(Optional.empty());
    given(passwordEncoder.encode(password)).willReturn("encodedPassword");
    AppUser appUser = mock(AppUser.class);
    UUID uuid = UUID.randomUUID();
    given(appUser.getUuid()).willReturn(uuid);
    given(appUserRepository.saveAndFlush(any())).willReturn(appUser);
    given(appUserSettingService.createAppUserSettings(any(), eq(uuid)))
        .willReturn(new AppUserSettingDto());

    // When
    var res = appUserSignupService.signup(appUserDto, bindingResult, model);

    // Then
    assertEquals(REDIRECT + LOGIN_VIEW, res);
  }

  //  @Test
  //  @DisplayName("Should create daily app user")
  //  void test10() {
  //    // Given
  //    AppUserDto appUserDto = new AppUserDto();
  //    appUserDto.setPassword("password");
  //    given(passwordEncoder.encode(appUserDto.getPassword())).willReturn("encodedPassword");
  //    AppUser appUser = mock(AppUser.class);
  //    given(appUser.getUuid()).willReturn(UUID.randomUUID());
  //    given(appUserRepository.save(any())).willReturn(appUser);
  //
  //    // When & then
  //    assertDoesNotThrow(() -> appUserSignupService.createDailyAppUser(appUserDto));
  //  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when signup with null argument")
  void test6(AppUserDto appUserDto, BindingResult bindingResult, Model model) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserSignupService.signup(appUserDto, bindingResult, model));
  }

  //  @ParameterizedTest
  //  @NullSource
  //  @DisplayName("Should throw when create daily app user with null argument")
  //  void test16(AppUserDto appUserDto) {
  //    assertThrows(
  //        IllegalArgumentException.class, () ->
  // appUserSignupService.createDailyAppUser(appUserDto));
  //  }
}
