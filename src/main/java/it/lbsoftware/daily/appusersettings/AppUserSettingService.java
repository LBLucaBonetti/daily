package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.Optional;

public interface AppUserSettingService {

  AppUserSettingDto createAppUserSettings(AppUserSettingDto appUserSettings, AppUser appUser);

  Optional<AppUserSettingDto> readAppUserSettings(AppUser appUser);

  Optional<AppUserSettingDto> updateAppUserSettings(
      AppUserSettingDto appUserSettings, AppUser appUser);
}
