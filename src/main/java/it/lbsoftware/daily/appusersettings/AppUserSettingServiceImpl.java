package it.lbsoftware.daily.appusersettings;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserSettingServiceImpl implements AppUserSettingService {

  private final AppUserSettingRepository appUserSettingRepository;
  private final AppUserSettingDtoMapper appUserSettingDtoMapper;

  @Override
  public AppUserSettingDto createAppUserSetting(AppUserSettingDto appUserSetting, UUID appUser) {
    AppUserSetting appUserSettingEntity =
        AppUserSetting.builder().appUser(appUser).lang(appUserSetting.getLang()).build();
    AppUserSetting savedAppUserSettingEntity = appUserSettingRepository.save(appUserSettingEntity);

    return appUserSettingDtoMapper.convertToDto(savedAppUserSettingEntity);
  }
}
