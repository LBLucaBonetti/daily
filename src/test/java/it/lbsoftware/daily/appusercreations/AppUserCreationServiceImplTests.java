package it.lbsoftware.daily.appusercreations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appuseractivations.AppUserActivation;
import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusersettings.AppUserSettingDto;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

class AppUserCreationServiceImplTests extends DailyAbstractUnitTests {

  @Mock private AppUserRepository appUserRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AppUserSettingService appUserSettingService;
  @Mock private AppUserActivationService appUserActivationService;
  private AppUserCreationServiceImpl appUserCreationService;

  @BeforeEach
  void beforeEach() {
    appUserCreationService =
        new AppUserCreationServiceImpl(
            appUserRepository, passwordEncoder, appUserSettingService, appUserActivationService);
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
    var uuid = UUID.randomUUID();
    appUserDto.setEmail("appuser@email.com");
    var appUser = mock(AppUser.class);
    when(appUser.getUuid()).thenReturn(uuid);
    when(appUserRepository.findByEmailIgnoreCase(appUserDto.getEmail()))
        .thenReturn(Optional.empty());
    when(appUserRepository.saveAndFlush(any())).thenReturn(appUser);
    when(appUserSettingService.createAppUserSettings(any(), eq(uuid)))
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
    var uuid = UUID.randomUUID();
    var lang = "en-US";
    appUserDto.setEmail("appuser@gmail.com");
    appUserDto.setLang(lang);
    var appUser = mock(AppUser.class);
    when(appUser.getUuid()).thenReturn(uuid);
    when(appUserRepository.findByEmailIgnoreCase(appUserDto.getEmail()))
        .thenReturn(Optional.empty());
    when(appUserRepository.saveAndFlush(any())).thenReturn(appUser);
    var appUserSettingDto = new AppUserSettingDto();
    appUserSettingDto.setLang(lang);
    when(appUserSettingService.createAppUserSettings(any(), eq(uuid)))
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
    var lang = "en-US";
    appUserDto.setEmail("appuser@email.com");
    appUserDto.setLang(lang);
    var appUser = mock(AppUser.class);
    when(appUser.getUuid()).thenReturn(uuid);
    when(appUserRepository.findByEmailIgnoreCase(appUserDto.getEmail()))
        .thenReturn(Optional.empty());
    when(appUserRepository.saveAndFlush(any())).thenReturn(appUser);
    var appUserSettingDto = new AppUserSettingDto();
    appUserSettingDto.setLang(lang);
    when(appUserSettingService.createAppUserSettings(any(), eq(uuid)))
        .thenReturn(appUserSettingDto);
    var appUserActivation = AppUserActivation.builder().activationCode(uuid).build();
    when(appUserActivationService.createAppUserActivation(any()))
        .thenReturn(Optional.of(appUserActivation));

    // When
    var res = appUserCreationService.createDailyAppUser(appUserDto);

    // Then
    assertTrue(res.isPresent());
    assertEquals(uuid.toString(), res.get());
  }
}
