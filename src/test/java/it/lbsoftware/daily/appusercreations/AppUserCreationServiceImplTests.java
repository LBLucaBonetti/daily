package it.lbsoftware.daily.appusercreations;

import static it.lbsoftware.daily.appusers.AppUser.AuthProvider.DAILY;
import static it.lbsoftware.daily.appusers.AppUser.AuthProvider.GOOGLE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appuseractivations.AppUserActivation;
import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appuserpasswords.AppUserPasswordSecurityService;
import it.lbsoftware.daily.appuserremovers.AppUserRemovalInformationRepository;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusersettings.AppUserSettingDto;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import it.lbsoftware.daily.exceptions.DailyNotEnoughSecurePasswordException;
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

class AppUserCreationServiceImplTests extends DailyAbstractUnitTests {

  @Mock private AppUserRepository appUserRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AppUserSettingService appUserSettingService;
  @Mock private AppUserActivationService appUserActivationService;
  @Mock private AppUserRemovalInformationRepository appUserRemovalInformationRepository;
  @Mock private AppUserPasswordSecurityService appUserPasswordSecurityService;
  private AppUserCreationServiceImpl appUserCreationService;

  private static Stream<Arguments> test9() {
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
    appUserCreationService =
        new AppUserCreationServiceImpl(
            appUserRepository,
            passwordEncoder,
            appUserSettingService,
            appUserActivationService,
            appUserRemovalInformationRepository,
            appUserPasswordSecurityService);
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when create daily app user with null app user dto")
  void test1(final AppUserDto appUserDto) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserCreationService.createDailyAppUser(appUserDto));
  }

  @Test
  @DisplayName("Should not create daily app user because already present and return empty optional")
  void test2() {
    // Given
    var appUserDto = new AppUserDto();
    appUserDto.setEmail("appuser@email.com");
    when(appUserRepository.findByEmailIgnoreCase(appUserDto.getEmail()))
        .thenReturn(Optional.of(new AppUser()));

    // When
    var res = appUserCreationService.createDailyAppUser(appUserDto);

    // Then
    assertTrue(res.isEmpty());
  }

  @Test
  @DisplayName("Should not create daily app user because save fails and throw")
  void test3() {
    // Given
    var appUserDto = new AppUserDto();
    appUserDto.setEmail("appuser@email.com");
    when(appUserRepository.findByEmailIgnoreCase(appUserDto.getEmail()))
        .thenReturn(Optional.empty());
    when(appUserRepository.saveAndFlush(any())).thenThrow(RuntimeException.class);

    // When and then
    assertThrows(
        RuntimeException.class, () -> appUserCreationService.createDailyAppUser(appUserDto));
  }

  @Test
  @DisplayName("Should not create daily app user because app user setting creation fails and throw")
  void test4() {
    // Given
    var appUserDto = new AppUserDto();
    var email = "appuser@email.com";
    appUserDto.setEmail(email);
    var appUser = mock(AppUser.class);
    when(appUserRepository.findByEmailIgnoreCase(appUserDto.getEmail()))
        .thenReturn(Optional.empty());
    when(appUserRepository.saveAndFlush(any())).thenReturn(appUser);
    when(appUserSettingService.createAppUserSettings(any(), eq(appUser)))
        .thenThrow(RuntimeException.class);

    // When and then
    assertThrows(
        RuntimeException.class, () -> appUserCreationService.createDailyAppUser(appUserDto));
  }

  @Test
  @DisplayName(
      "Should not create daily app user because app user activation creation fails and return empty optional")
  void test5() {
    // Given
    var appUserDto = new AppUserDto();
    var email = "appuser@gmail.com";
    var lang = "en-US";
    appUserDto.setEmail(email);
    appUserDto.setLang(lang);
    var appUser = mock(AppUser.class);
    when(appUserRepository.findByEmailIgnoreCase(appUserDto.getEmail()))
        .thenReturn(Optional.empty());
    when(appUserRepository.saveAndFlush(any())).thenReturn(appUser);
    var appUserSettingDto = new AppUserSettingDto();
    appUserSettingDto.setLang(lang);
    when(appUserSettingService.createAppUserSettings(any(), eq(appUser)))
        .thenReturn(appUserSettingDto);
    when(appUserActivationService.createAppUserActivation(any())).thenReturn(Optional.empty());

    // When
    var res = appUserCreationService.createDailyAppUser(appUserDto);

    // Then
    assertTrue(res.isEmpty());
  }

  @Test
  @DisplayName("Should create daily app user and return app user activation code optional")
  void test6() {
    // Given
    var appUserDto = new AppUserDto();
    var uuid = UUID.randomUUID();
    var email = "appuser@gmail.com";
    var lang = "en-US";
    appUserDto.setEmail(email);
    appUserDto.setLang(lang);
    var appUser = mock(AppUser.class);
    when(appUserRepository.findByEmailIgnoreCase(appUserDto.getEmail()))
        .thenReturn(Optional.empty());
    when(appUserRepository.saveAndFlush(any())).thenReturn(appUser);
    var appUserSettingDto = new AppUserSettingDto();
    appUserSettingDto.setLang(lang);
    when(appUserSettingService.createAppUserSettings(any(), eq(appUser)))
        .thenReturn(appUserSettingDto);
    var appUserActivation = AppUserActivation.builder().activationCode(uuid).build();
    when(appUserActivationService.createAppUserActivation(any()))
        .thenReturn(Optional.of(appUserActivation));

    // When
    var res = appUserCreationService.createDailyAppUser(appUserDto);

    // Then
    assertTrue(res.isPresent());
    assertEquals(uuid, res.get());
    verify(appUserRemovalInformationRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Should throw when create oauth2 app user with daily auth provider")
  void test7() {
    // Given
    AuthProvider authProvider = AuthProvider.DAILY;
    AppUserDto appUserDto = new AppUserDto();

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                appUserCreationService.createOrUpdateOauth2AppUser(
                    appUserDto, authProvider, "authProviderId"));

    // Then
    assertNotNull(res.getMessage());
  }

  @Test
  @DisplayName("Should create oauth2 app user")
  void test8() {
    // Given
    var authProviderId = "authProviderId";
    var authProvider = GOOGLE;
    AppUserDto appUserDto = new AppUserDto();
    given(appUserRepository.findByAuthProviderIdAndAuthProvider(authProviderId, authProvider))
        .willReturn(Optional.empty());
    AppUser appUser = mock(AppUser.class);
    given(appUserRepository.saveAndFlush(any())).willReturn(appUser);

    // When
    assertDoesNotThrow(
        () ->
            appUserCreationService.createOrUpdateOauth2AppUser(
                appUserDto, authProvider, authProviderId));

    // Then
    verify(appUserSettingService, times(1)).createAppUserSettings(any(), eq(appUser));
    verify(appUserRemovalInformationRepository, times(1)).save(any());
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when create oauth2 app user with null argument")
  void test9(AppUserDto appUserDto, AuthProvider authProvider, String authProviderId) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            appUserCreationService.createOrUpdateOauth2AppUser(
                appUserDto, authProvider, authProviderId));
  }

  @Test
  @DisplayName("Should update oauth2 app user with new data")
  void test10() {
    // Given
    var newEmail = "newemail@gmail.com";
    var appUserDto = new AppUserDto();
    appUserDto.setEmail(newEmail);
    var oldEmail = "oldemail@gmail.com";
    var appUser = AppUser.builder().email(oldEmail).build();
    var authProviderId = UUID.randomUUID().toString();
    var authProvider = GOOGLE;
    given(appUserRepository.findByAuthProviderIdAndAuthProvider(authProviderId, authProvider))
        .willReturn(Optional.of(appUser));

    // When
    appUserCreationService.createOrUpdateOauth2AppUser(appUserDto, authProvider, authProviderId);

    // Then
    assertEquals(newEmail, appUser.getEmail());
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(newEmail);
  }

  @Test
  @DisplayName("Should not update oauth2 app user with new data")
  void test11() {
    // Given
    var email = "newemail@gmail.com";
    var appUserDto = new AppUserDto();
    appUserDto.setEmail(email);
    var appUser = mock(AppUser.class);
    given(appUser.getEmail()).willReturn(email);
    var authProviderId = UUID.randomUUID().toString();
    var authProvider = GOOGLE;
    given(appUserRepository.findByAuthProviderIdAndAuthProvider(authProviderId, authProvider))
        .willReturn(Optional.of(appUser));

    // When
    appUserCreationService.createOrUpdateOauth2AppUser(appUserDto, authProvider, authProviderId);

    // Then
    assertEquals(email, appUser.getEmail());
    verify(appUser, times(0)).setEmail(any());
    verify(appUserRepository, times(0)).findByEmailIgnoreCase(email);
  }

  @Test
  @DisplayName("Should throw when update oauth2 app user with already-existent e-mail")
  void test12() {
    // Given
    var newEmail = "newemail@gmail.com";
    var appUserDto = new AppUserDto();
    appUserDto.setEmail(newEmail);
    var oldEmail = "oldemail@gmail.com";
    var appUser = AppUser.builder().email(oldEmail).build();
    var authProviderId = UUID.randomUUID().toString();
    var authProvider = GOOGLE;
    var alreadyExistentAppUser = AppUser.builder().email(newEmail).authProvider(DAILY).build();
    given(appUserRepository.findByAuthProviderIdAndAuthProvider(authProviderId, authProvider))
        .willReturn(Optional.of(appUser));
    given(appUserRepository.findByEmailIgnoreCase(newEmail))
        .willReturn(Optional.of(alreadyExistentAppUser));

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                appUserCreationService.createOrUpdateOauth2AppUser(
                    appUserDto, authProvider, authProviderId));

    // Then
    assertNotNull(res);
    assertEquals(oldEmail, appUser.getEmail());
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(newEmail);
  }

  @Test
  @DisplayName("Should throw when create oauth2 app user with already-existent e-mail")
  void test13() {
    // Given
    var email = "anemail@gmail.com";
    var appUserDto = new AppUserDto();
    appUserDto.setEmail(email);
    var authProviderId = UUID.randomUUID().toString();
    var authProvider = GOOGLE;
    var alreadyExistentAppUser = AppUser.builder().email(email).authProvider(DAILY).build();
    given(appUserRepository.findByAuthProviderIdAndAuthProvider(authProviderId, authProvider))
        .willReturn(Optional.empty());
    given(appUserRepository.findByEmailIgnoreCase(email))
        .willReturn(Optional.of(alreadyExistentAppUser));

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                appUserCreationService.createOrUpdateOauth2AppUser(
                    appUserDto, authProvider, authProviderId));

    // Then
    assertNotNull(res);
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(email);
    verify(appUserRepository, times(0)).saveAndFlush(any());
    verify(appUserSettingService, times(0)).createAppUserSettings(any(), any());
    verify(appUserRemovalInformationRepository, times(0)).save(any());
  }

  @Test
  @DisplayName(
      "Should not create daily app user because of insecure password and return empty optional")
  void test14() {
    // Given
    var insecurePassword = "password";
    var email = "appuser@email.com";
    var appUserDto = new AppUserDto();
    appUserDto.setEmail(email);
    appUserDto.setPassword(insecurePassword);
    appUserDto.setPasswordConfirmation(insecurePassword);
    doThrow(new DailyNotEnoughSecurePasswordException(""))
        .when(appUserPasswordSecurityService)
        .check(insecurePassword);

    // When
    var res = appUserCreationService.createDailyAppUser(appUserDto);

    // Then
    assertEquals(Optional.empty(), res);
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(email);
    verify(appUserPasswordSecurityService, times(1)).check(insecurePassword);
    verify(appUserRepository, times(0)).saveAndFlush(any());
    verify(appUserSettingService, times(0)).createAppUserSettings(any(), any());
    verify(appUserRemovalInformationRepository, times(0)).save(any());
    verify(appUserActivationService, times(0)).createAppUserActivation(any());
  }
}
