package it.lbsoftware.daily.appusersettings;

import java.util.UUID;

public final class AppUserSettingTestUtils {
  private AppUserSettingTestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * AppUserSettingDto generator
   *
   * @param uuid Unique identifier
   * @param lang Lang content
   * @return The created AppUserSettingDto
   */
  public static AppUserSettingDto createAppUserSettingDto(final UUID uuid, final String lang) {
    AppUserSettingDto appUserSettingDto = new AppUserSettingDto();
    appUserSettingDto.setUuid(uuid);
    appUserSettingDto.setLang(lang);
    return appUserSettingDto;
  }

  /**
   * AppUserSetting generator; the created instance is not persisted
   *
   * @param lang Lang content
   * @param appUser The creator
   * @return The created AppUserSetting
   */
  public static AppUserSetting createAppUserSetting(final String lang, final UUID appUser) {
    return AppUserSetting.builder().lang(lang).appUser(appUser).build();
  }
}
