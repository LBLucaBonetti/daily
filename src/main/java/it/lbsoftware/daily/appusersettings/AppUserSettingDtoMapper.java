package it.lbsoftware.daily.appusersettings;

import org.mapstruct.Mapper;

/** Mapper for {@link AppUserSetting} entities. */
@Mapper
public interface AppUserSettingDtoMapper {

  AppUserSettingDto convertToDto(AppUserSetting appUserSetting);
}
