package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.bases.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** {@link AppUserSetting} dto. */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class AppUserSettingDto extends BaseDto {

  private String lang;
}
