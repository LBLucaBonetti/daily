package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_FULLNAME;
import static it.lbsoftware.daily.config.Constants.LOGIN_VIEW;
import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static it.lbsoftware.daily.config.Constants.SIGNUP_VIEW;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusersactivations.AppUserActivationService;
import it.lbsoftware.daily.appusersettings.AppUserSettingDto;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import it.lbsoftware.daily.emails.EmailService;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@DisplayName("AppUserServiceImpl unit tests")
class AppUserServiceImplTests extends DailyAbstractUnitTests {

  @Mock private AppUserRepository appUserRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AppUserSettingService appUserSettingService;
  @Mock private EmailService emailService;
  @Mock private AppUserActivationService appUserActivationService;
  private AppUserServiceImpl appUserService;

  private static Stream<Arguments> test15() {
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

  private static Stream<Arguments> test17() {
    // AppUserDto, authProvider, authProviderId
    AppUserDto appUserDto = new AppUserDto();
    AuthProvider authProvider = AuthProvider.DAILY;
    String authProviderId = "authProviderId";
    return Stream.of(
        arguments(null, null, null),
        arguments(null, null, authProviderId),
        arguments(null, authProvider, null),
        arguments(null, authProvider, authProviderId),
        arguments(appUserDto, null, null),
        arguments(appUserDto, null, authProviderId),
        arguments(appUserDto, authProvider, null));
  }

  @BeforeEach
  void beforeEach() {
    appUserService =
        new AppUserServiceImpl(
            appUserRepository,
            passwordEncoder,
            appUserSettingService,
            emailService,
            appUserActivationService);
  }

  @Test
  @DisplayName("Should not get uuid and throw")
  void test1() {
    // Given
    OidcUser principal = mock(OidcUser.class);
    given(principal.getEmail()).willReturn(APP_USER_EMAIL);
    given(principal.getFullName()).willReturn(APP_USER_FULLNAME);
    given(appUserRepository.findByEmailIgnoreCase(APP_USER_EMAIL)).willReturn(Optional.empty());

    // When
    var res = assertThrows(NoSuchElementException.class, () -> appUserService.getUuid(principal));

    // Then
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(APP_USER_EMAIL);
    assertNotNull(res.getMessage());
  }

  @Test
  @DisplayName("Should get uuid and return uuid")
  void test2() {
    // Given
    OidcUser principal = mock(OidcUser.class);
    AppUser appUser = mock(AppUser.class);
    UUID uuid = UUID.randomUUID();
    given(principal.getEmail()).willReturn(APP_USER_EMAIL);
    given(principal.getFullName()).willReturn(APP_USER_FULLNAME);
    given(appUserRepository.findByEmailIgnoreCase(APP_USER_EMAIL)).willReturn(Optional.of(appUser));
    given(appUser.getUuid()).willReturn(uuid);

    // When
    var res = appUserService.getUuid(principal);

    // Then
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(APP_USER_EMAIL);
    assertEquals(uuid, res);
  }

  @Test
  @DisplayName("Should get app user info with OidcUser")
  void test3() {
    // Given
    OidcUser principal = mock(OidcUser.class);
    given(principal.getEmail()).willReturn(APP_USER_EMAIL);
    given(principal.getFullName()).willReturn(APP_USER_FULLNAME);

    // When
    var res = appUserService.getAppUserInfo(principal);

    // Then
    assertEquals(APP_USER_EMAIL, res.email());
    assertEquals(APP_USER_FULLNAME, res.fullName());
  }

  @Test
  @DisplayName("Should get app user info with AppUserDetails")
  void test4() {
    // Given
    AppUserDetails principal = mock(AppUserDetails.class);
    given(principal.getUsername()).willReturn(APP_USER_EMAIL);
    given(principal.getFullname()).willReturn(APP_USER_FULLNAME);

    // When
    var res = appUserService.getAppUserInfo(principal);

    // Then
    assertEquals(APP_USER_EMAIL, res.email());
    assertEquals(APP_USER_FULLNAME, res.fullName());
  }

  @Test
  @DisplayName("Should return signup when binding result has errors")
  void test5() {
    // Given
    AppUserDto appUserDto = mock(AppUserDto.class);
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    given(bindingResult.hasErrors()).willReturn(true);

    // When
    var res = appUserService.signup(appUserDto, bindingResult, model);

    // Then
    assertEquals(SIGNUP_VIEW, res);
  }

  @Test
  @DisplayName("Should return signup when passwords do not match")
  void test6() {
    // Given
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setPassword("password");
    appUserDto.setPasswordConfirmation("differentPassword");
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    given(bindingResult.hasErrors()).willReturn(false);

    // When
    var res = appUserService.signup(appUserDto, bindingResult, model);

    // Then
    assertEquals(SIGNUP_VIEW, res);
  }

  @Test
  @DisplayName("Should return signup when email is oauth2")
  void test7() {
    // Given
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setPassword("password");
    appUserDto.setPasswordConfirmation("password");
    BindingResult bindingResult = mock(BindingResult.class);
    Model model = mock(Model.class);
    given(bindingResult.hasErrors()).willReturn(false);
    appUserDto.setEmail("email@gmail.com");

    // When
    var res = appUserService.signup(appUserDto, bindingResult, model);

    // Then
    assertEquals(SIGNUP_VIEW, res);
  }

  @Test
  @DisplayName("Should return signup when email is taken")
  void test8() {
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
    var res = appUserService.signup(appUserDto, bindingResult, model);

    // Then
    assertEquals(SIGNUP_VIEW, res);
  }

  @Test
  @DisplayName("Should return login when signup is performed")
  void test9() {
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
    given(appUserRepository.save(any())).willReturn(appUser);
    given(appUserSettingService.createAppUserSettings(any(), eq(uuid)))
        .willReturn(new AppUserSettingDto());

    // When
    var res = appUserService.signup(appUserDto, bindingResult, model);

    // Then
    assertEquals(REDIRECT + LOGIN_VIEW, res);
  }

  @Test
  @DisplayName("Should create daily app user")
  void test10() {
    // Given
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setPassword("password");
    given(passwordEncoder.encode(appUserDto.getPassword())).willReturn("encodedPassword");
    AppUser appUser = mock(AppUser.class);
    given(appUser.getUuid()).willReturn(UUID.randomUUID());
    given(appUserRepository.save(any())).willReturn(appUser);

    // When & then
    assertDoesNotThrow(() -> appUserService.createDailyAppUser(appUserDto));
  }

  @Test
  @DisplayName("Should throw when create oauth2 app user with daily auth provider")
  void test11() {
    // Given
    AuthProvider authProvider = AuthProvider.DAILY;
    AppUserDto appUserDto = new AppUserDto();

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () -> appUserService.createOauth2AppUser(appUserDto, authProvider, "authProviderId"));

    // Then
    assertNull(res.getMessage());
  }

  @Test
  @DisplayName("Should create oauth2 app user")
  void test12() {
    // Given
    AppUserDto appUserDto = new AppUserDto();
    AppUser appUser = mock(AppUser.class);
    given(appUser.getUuid()).willReturn(UUID.randomUUID());
    given(appUserRepository.save(any())).willReturn(appUser);

    // When & then
    assertDoesNotThrow(
        () ->
            appUserService.createOauth2AppUser(appUserDto, AuthProvider.GOOGLE, "authProviderId"));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when get uuid with null argument")
  void test13(Object principal) {
    assertThrows(IllegalArgumentException.class, () -> appUserService.getUuid(principal));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when get app user info with null argument")
  void test14(Object principal) {
    assertThrows(IllegalArgumentException.class, () -> appUserService.getAppUserInfo(principal));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when signup with null argument")
  void test15(AppUserDto appUserDto, BindingResult bindingResult, Model model) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserService.signup(appUserDto, bindingResult, model));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when create daily app user with null argument")
  void test16(AppUserDto appUserDto) {
    assertThrows(
        IllegalArgumentException.class, () -> appUserService.createDailyAppUser(appUserDto));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when create oauth2 app user with null argument")
  void test17(AppUserDto appUserDto, AuthProvider authProvider, String authProviderId) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserService.createOauth2AppUser(appUserDto, authProvider, authProviderId));
  }

  @Test
  @DisplayName("Should throw when get app user info and app user is not recognized")
  void test18() {
    // Given
    String appUser = "appUser";

    // When & then
    assertThrows(IllegalStateException.class, () -> appUserService.getAppUserInfo(appUser));
  }
}
