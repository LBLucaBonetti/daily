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
      @NonNull AppUserSettingDto appUserSetting, @NonNull UUID appUser) {
    AppUserSetting appUserSettingEntity =
        AppUserSetting.builder().appUser(appUser).lang(appUserSetting.getLang()).build();
    AppUserSetting savedAppUserSettingEntity = appUserSettingRepository.save(appUserSettingEntity);

    return appUserSettingDtoMapper.convertToDto(savedAppUserSettingEntity);
  }

  @Override
  public Optional<AppUserSettingDto> readAppUserSettings(@NonNull UUID appUser) {
    return appUserSettingRepository
        .findByAppUser(appUser)
        .map(appUserSettingDtoMapper::convertToDto);
  }

  @Override
  public Optional<AppUserSettingDto> updateAppUserSettings(
      @NonNull AppUserSettingDto appUserSetting, @NonNull UUID appUser) {
    return appUserSettingRepository
        .findByAppUser(appUser)
        .map(
            prevAppUserSetting -> {
              Optional.ofNullable(appUserSetting.getLang()).ifPresent(prevAppUserSetting::setLang);
              return appUserSettingRepository.saveAndFlush(prevAppUserSetting);
            })
        .map(appUserSettingDtoMapper::convertToDto);
  }
}
