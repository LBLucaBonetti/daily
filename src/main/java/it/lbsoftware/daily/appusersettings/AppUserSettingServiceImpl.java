package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppUserSettingServiceImpl implements AppUserSettingService {

  private final AppUserSettingRepository appUserSettingRepository;
  private final AppUserSettingDtoMapper appUserSettingDtoMapper;

  @Override
  @Transactional
  public AppUserSettingDto createAppUserSettings(
      @NonNull AppUserSettingDto appUserSettings, @NonNull AppUser appUser) {
    AppUserSetting appUserSettingsEntity =
        AppUserSetting.builder().appUser(appUser).lang(appUserSettings.getLang()).build();
    AppUserSetting savedAppUserSettingsEntity =
        appUserSettingRepository.saveAndFlush(appUserSettingsEntity);

    return appUserSettingDtoMapper.convertToDto(savedAppUserSettingsEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<AppUserSettingDto> readAppUserSettings(@NonNull AppUser appUser) {
    return appUserSettingRepository
        .findByAppUser(appUser)
        .map(appUserSettingDtoMapper::convertToDto);
  }

  @Override
  @Transactional
  public Optional<AppUserSettingDto> updateAppUserSettings(
      @NonNull AppUserSettingDto appUserSettings, @NonNull AppUser appUser) {
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
