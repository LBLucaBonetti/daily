package it.lbsoftware.daily.appusersettings;

import java.util.Optional;
import java.util.UUID;

public interface AppUserSettingService {

  AppUserSettingDto createAppUserSettings(AppUserSettingDto appUserSettings, UUID appUser);

  Optional<AppUserSettingDto> readAppUserSettings(UUID appUser);

  Optional<AppUserSettingDto> updateAppUserSettings(
      AppUserSettingDto appUserSettings, UUID appUser);
}
