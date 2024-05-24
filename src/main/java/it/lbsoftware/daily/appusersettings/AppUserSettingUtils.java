package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.appusers.AppUserDto;
import lombok.NonNull;

/** Utilities to deal with {@link AppUserSetting} entities. */
public final class AppUserSettingUtils {

  private AppUserSettingUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  /**
   * Creates an {@link AppUserSettingDto} from an {@link AppUserDto}.
   *
   * @param appUser The source to create the settings dto from
   * @return A dto with data configured according to the provided app user dto
   */
  public static AppUserSettingDto getAppUserSettings(@NonNull final AppUserDto appUser) {
    AppUserSettingDto appUserSetting = new AppUserSettingDto();
    appUserSetting.setLang(appUser.getLang());
    return appUserSetting;
  }
}
