package it.lbsoftware.daily.appusersettings;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserSettingServiceImpl implements AppUserSettingService {

  private final AppUserSettingRepository appUserSettingRepository;
  private final AppUserSettingDtoMapper appUserSettingDtoMapper;

  @Override
  public AppUserSettingDto createAppUserSettings(
      @NonNull AppUserSettingDto appUserSettings, @NonNull UUID appUser) {
    AppUserSetting appUserSettingsEntity =
        AppUserSetting.builder().appUser(appUser).lang(appUserSettings.getLang()).build();
    AppUserSetting savedAppUserSettingsEntity =
        appUserSettingRepository.save(appUserSettingsEntity);

    return appUserSettingDtoMapper.convertToDto(savedAppUserSettingsEntity);
  }

  @Override
  public Optional<AppUserSettingDto> readAppUserSettings(@NonNull UUID appUser) {
    return appUserSettingRepository
        .findByAppUser(appUser)
        .map(appUserSettingDtoMapper::convertToDto);
  }

  @Override
  public Optional<AppUserSettingDto> updateAppUserSettings(
      @NonNull AppUserSettingDto appUserSettings, @NonNull UUID appUser) {
    return appUserSettingRepository
        .findByAppUser(appUser)
        .map(
            prevAppUserSettings -> {
              Optional.ofNullable(appUserSettings.getLang())
                  .ifPresent(prevAppUserSettings::setLang);
              return appUserSettingRepository.saveAndFlush(prevAppUserSettings);
            })
        .map(appUserSettingDtoMapper::convertToDto);
  }
}
