package it.lbsoftware.daily.appusercreations;

import static it.lbsoftware.daily.appusers.AppUserUtils.isDailyAuthProvider;
import static it.lbsoftware.daily.appusersettings.AppUserSettingUtils.getAppUserSettings;

import it.lbsoftware.daily.appuseractivations.AppUserActivation;
import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appuserremovers.AppUserRemovalInformation;
import it.lbsoftware.daily.appuserremovers.AppUserRemovalInformationRepository;
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
  private final AppUserRemovalInformationRepository appUserRemovalInformationRepository;

  @Override
  @Transactional
  public Optional<UUID> createDailyAppUser(@NonNull final AppUserDto appUserDto) {
    if (appUserRepository.findByEmailIgnoreCase(appUserDto.getEmail()).isPresent()) {
      return Optional.empty();
    }
    var appUser = buildDailyAppUser(appUserDto);
    // Save the AppUser
    var savedAppUser = appUserRepository.saveAndFlush(appUser);
    // Create settings
    appUserSettingService.createAppUserSettings(getAppUserSettings(appUserDto), savedAppUser);
    // Create removal information
    appUserRemovalInformationRepository.save(
        AppUserRemovalInformation.builder().appUser(appUser).failures(0).notifiedAt(null).build());
    // Create the activation link
    return appUserActivationService
        .createAppUserActivation(savedAppUser)
        .map(AppUserActivation::getActivationCode);
  }

  private AppUser buildDailyAppUser(final AppUserDto appUserDto) {
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
  public void createOrUpdateOauth2AppUser(
      @NonNull final AppUserDto appUserDto,
      @NonNull final AuthProvider authProvider,
      @NonNull final String authProviderId) {
    if (isDailyAuthProvider(authProvider)) {
      throw new IllegalArgumentException(
          "Invalid auth provider for OAuth2 flow: " + AuthProvider.DAILY);
    }
    appUserRepository
        .findByAuthProviderIdAndAuthProvider(authProviderId, authProvider)
        .ifPresentOrElse(
            (AppUser appUser) -> updateOauth2AppUser(appUser, appUserDto),
            () -> createOauth2AppUser(appUserDto, authProvider, authProviderId));
  }

  private AppUser buildOauth2AppUser(
      final AppUserDto appUserDto, final AuthProvider authProvider, final String authProviderId) {
    return AppUser.builder()
        .authProvider(authProvider)
        .authProviderId(authProviderId)
        .enabled(true)
        .email(appUserDto.getEmail())
        .build();
  }

  /**
   * Updates an OAuth2 {@code AppUser} with the provided data; this method assumes the {@code
   * AppUser} already existed and update data has already been validated
   *
   * @param appUser The {@code AppUser} to update
   * @param appUserDto The new data to update the {@code AppUser} with
   */
  private void updateOauth2AppUser(final AppUser appUser, final AppUserDto appUserDto) {
    var previousEmail = appUser.getEmail();
    var newEmail = appUserDto.getEmail();
    if (!previousEmail.equals(newEmail)) {
      appUser.setEmail(newEmail);
      log.info(
          "The OAuth2 AppUser changed e-mail address from " + previousEmail + " to " + newEmail);
    }
  }

  private void createOauth2AppUser(
      final AppUserDto appUserDto, final AuthProvider authProvider, final String authProviderId) {
    var appUser = buildOauth2AppUser(appUserDto, authProvider, authProviderId);
    var savedAppUser = appUserRepository.saveAndFlush(appUser);
    // Create settings
    appUserSettingService.createAppUserSettings(getAppUserSettings(appUserDto), savedAppUser);
    // Create removal information
    appUserRemovalInformationRepository.save(
        AppUserRemovalInformation.builder().appUser(appUser).failures(0).notifiedAt(null).build());
  }
}
