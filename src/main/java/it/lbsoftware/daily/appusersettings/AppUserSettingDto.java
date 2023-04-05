package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.bases.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class AppUserSettingDto extends BaseDto {

  private String lang;
}
