package it.lbsoftware.daily.appusersettings;

import java.util.UUID;

public interface AppUserSettingService {

  AppUserSettingDto createAppUserSetting(AppUserSettingDto appUserSetting, UUID appUser);
}
