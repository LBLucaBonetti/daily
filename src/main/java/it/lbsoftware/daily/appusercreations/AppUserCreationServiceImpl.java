package it.lbsoftware.daily.appusercreations;

import static it.lbsoftware.daily.appusersettings.AppUserSettingUtils.getAppUserSettings;

import it.lbsoftware.daily.appuseractivations.AppUserActivation;
import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CommonsLog
class AppUserCreationServiceImpl implements AppUserCreationService {

  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final AppUserSettingService appUserSettingService;
  private final AppUserActivationService appUserActivationService;

  @Override
  @Transactional
  public Optional<String> createDailyAppUser(@NonNull final AppUserDto appUserDto) {
    if (appUserRepository.findByEmailIgnoreCase(appUserDto.getEmail()).isPresent()) {
      return Optional.empty();
    }
    var appUser = createAppUser(appUserDto);
    // Save the AppUser
    var appUserUuid = appUserRepository.saveAndFlush(appUser).getUuid();
    // Create settings
    appUserSettingService.createAppUserSettings(getAppUserSettings(appUserDto), appUserUuid);
    // Create the activation link
    return appUserActivationService
        .createAppUserActivation(appUser)
        .map(AppUserActivation::getActivationCode)
        .map(UUID::toString);
  }

  private AppUser createAppUser(final AppUserDto appUserDto) {
    return AppUser.builder()
        .authProvider(AuthProvider.DAILY)
        .enabled(false)
        .firstName(appUserDto.getFirstName())
        .lastName(appUserDto.getLastName())
        .password(passwordEncoder.encode(appUserDto.getPassword()))
        .email(appUserDto.getEmail())
        .build();
  }

  @Override
  @Transactional
  public void createOauth2AppUser(
      @NonNull AppUserDto appUserDto,
      @NonNull AuthProvider authProvider,
      @NonNull String authProviderId) {
    if (AuthProvider.DAILY.equals(authProvider)) {
      throw new IllegalArgumentException();
    }
    AppUser appUser =
        AppUser.builder()
            .authProvider(authProvider)
            .authProviderId(authProviderId)
            .enabled(true)
            .email(appUserDto.getEmail())
            .build();
    final UUID appUserUuid = appUserRepository.save(appUser).getUuid();
    appUserSettingService.createAppUserSettings(getAppUserSettings(appUserDto), appUserUuid);
  }
}
