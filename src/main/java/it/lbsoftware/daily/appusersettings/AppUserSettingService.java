package it.lbsoftware.daily.appusersettings;

import java.util.Optional;
import java.util.UUID;

public interface AppUserSettingService {

  AppUserSettingDto createAppUserSetting(AppUserSettingDto appUserSetting, UUID appUser);

  Optional<AppUserSettingDto> readAppUserSettings(UUID appUser);

  Optional<AppUserSettingDto> updateAppUserSettings(AppUserSettingDto appUserSetting, UUID appUser);
}
