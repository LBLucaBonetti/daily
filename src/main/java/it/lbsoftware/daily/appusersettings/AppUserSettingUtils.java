package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.appusers.AppUserDto;
import lombok.NonNull;

public final class AppUserSettingUtils {

  private AppUserSettingUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  public static AppUserSettingDto getAppUserSettings(@NonNull final AppUserDto appUser) {
    AppUserSettingDto appUserSetting = new AppUserSettingDto();
    appUserSetting.setLang(appUser.getLang());
    return appUserSetting;
  }
}
