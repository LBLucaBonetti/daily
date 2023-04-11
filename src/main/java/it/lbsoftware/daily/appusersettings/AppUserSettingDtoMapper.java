package it.lbsoftware.daily.appusersettings;

import org.mapstruct.Mapper;

@Mapper
public interface AppUserSettingDtoMapper {

  AppUserSettingDto convertToDto(AppUserSetting appUserSetting);
}
