package it.lbsoftware.daily.appusersettings;

import static it.lbsoftware.daily.appusersettings.AppUserSettingTestUtils.createAppUserSetting;
import static it.lbsoftware.daily.appusersettings.AppUserSettingTestUtils.createAppUserSettingDto;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
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

@DisplayName("AppUserSettingServiceImpl unit tests")
class AppUserSettingServiceImplTests extends DailyAbstractUnitTests {

  private static final String LANG = "en-US";
  private static final UUID APP_USER = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final String OTHER_LANG = "it";
  @Mock private AppUserSettingRepository appUserSettingRepository;
  @Mock private AppUserSettingDtoMapper appUserSettingDtoMapper;
  private AppUserSettingServiceImpl appUserSettingService;

  private static Stream<Arguments> test6() {
    // AppUserSetting, appUser
    AppUserSettingDto appUserSetting = createAppUserSettingDto(null, LANG);
    return Stream.of(
        arguments(null, null), arguments(null, APP_USER), arguments(appUserSetting, null));
  }

  private static Stream<Arguments> test8() {
    // AppUserSetting, appUser
    AppUserSettingDto appUserSetting = createAppUserSettingDto(null, LANG);
    return Stream.of(
        arguments(null, null), arguments(null, APP_USER), arguments(appUserSetting, null));
  }

  @BeforeEach
  void beforeEach() {
    appUserSettingService =
        new AppUserSettingServiceImpl(appUserSettingRepository, appUserSettingDtoMapper);
  }

  @Test
  @DisplayName("Should create app user settings and return app user setting")
  void test1() {
    // Given
    AppUserSettingDto appUserSetting = createAppUserSettingDto(null, LANG);
    AppUserSetting savedAppUserSettingEntity = createAppUserSetting(LANG, APP_USER);
    AppUserSettingDto appUserSettingDto = createAppUserSettingDto(UUID.randomUUID(), LANG);
    given(appUserSettingRepository.save(any())).willReturn(savedAppUserSettingEntity);
    given(appUserSettingDtoMapper.convertToDto(savedAppUserSettingEntity))
        .willReturn(appUserSettingDto);

    // When
    AppUserSettingDto res = appUserSettingService.createAppUserSettings(appUserSetting, APP_USER);

    // Then
    verify(appUserSettingRepository, times(1)).save(any());
    verify(appUserSettingDtoMapper, times(1)).convertToDto(savedAppUserSettingEntity);
    assertEquals(LANG, res.getLang());
    assertNotNull(res.getUuid());
  }

  @Test
  @DisplayName("Should not read app user settings and return empty optional")
  void test2() {
    // Given
    given(appUserSettingRepository.findByAppUser(APP_USER)).willReturn(Optional.empty());

    // When
    Optional<AppUserSettingDto> res = appUserSettingService.readAppUserSettings(APP_USER);

    // Then
    verify(appUserSettingRepository, times(1)).findByAppUser(APP_USER);
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName("Should read app user settings and return app user setting")
  void test3() {
    // Given
    AppUserSetting appUserSetting = createAppUserSetting(LANG, APP_USER);
    AppUserSettingDto appUserSettingDto = createAppUserSettingDto(UUID.randomUUID(), LANG);
    Optional<AppUserSetting> appUserSettings = Optional.of(appUserSetting);
    given(appUserSettingRepository.findByAppUser(APP_USER)).willReturn(appUserSettings);
    given(appUserSettingDtoMapper.convertToDto(appUserSetting)).willReturn(appUserSettingDto);

    // When
    Optional<AppUserSettingDto> res = appUserSettingService.readAppUserSettings(APP_USER);

    // Then
    verify(appUserSettingRepository, times(1)).findByAppUser(APP_USER);
    verify(appUserSettingDtoMapper, times(1)).convertToDto(appUserSetting);
    assertEquals(appUserSettingDto, res.get());
  }

  @Test
  @DisplayName("Should not update app user settings and return empty optional")
  void test4() {
    // Given
    Optional<AppUserSetting> appUserSettingOptional = Optional.empty();
    given(appUserSettingRepository.findByAppUser(APP_USER)).willReturn(appUserSettingOptional);

    // When
    Optional<AppUserSettingDto> res =
        appUserSettingService.updateAppUserSettings(
            createAppUserSettingDto(null, OTHER_LANG), APP_USER);

    // Then
    verify(appUserSettingRepository, times(1)).findByAppUser(APP_USER);
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName("Should update app user settings and return app user settings optional")
  void test5() {
    // Given
    AppUserSetting prevAppUserSetting = createAppUserSetting(LANG, APP_USER);
    AppUserSetting updatedAppUserSetting = createAppUserSetting(OTHER_LANG, APP_USER);
    AppUserSettingDto updatedAppUserSettingDto =
        createAppUserSettingDto(UUID.randomUUID(), OTHER_LANG);
    given(appUserSettingRepository.findByAppUser(APP_USER))
        .willReturn(Optional.of(prevAppUserSetting));
    given(appUserSettingRepository.saveAndFlush(prevAppUserSetting))
        .willReturn(updatedAppUserSetting);
    given(appUserSettingDtoMapper.convertToDto(updatedAppUserSetting))
        .willReturn(updatedAppUserSettingDto);

    // When
    Optional<AppUserSettingDto> res =
        appUserSettingService.updateAppUserSettings(updatedAppUserSettingDto, APP_USER);

    // Then
    verify(appUserSettingRepository, times(1)).findByAppUser(APP_USER);
    verify(appUserSettingRepository, times(1)).saveAndFlush(prevAppUserSetting);
    assertEquals(res, Optional.of(updatedAppUserSettingDto));
    assertEquals(OTHER_LANG, res.get().getLang());
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when create app user settings with null argument")
  void test6(AppUserSettingDto appUserSetting, UUID appUser) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserSettingService.createAppUserSettings(appUserSetting, appUser));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when read app user settings with null argument")
  void test7(UUID appUser) {
    assertThrows(
        IllegalArgumentException.class, () -> appUserSettingService.readAppUserSettings(appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when update app user settings with null argument")
  void test8(AppUserSettingDto appUserSetting, UUID appUser) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserSettingService.updateAppUserSettings(appUserSetting, appUser));
  }
}
