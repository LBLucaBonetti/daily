package it.lbsoftware.daily.appusercreations;

import static it.lbsoftware.daily.appusers.AppUserUtils.isDailyAuthProvider;
import static it.lbsoftware.daily.appusersettings.AppUserSettingUtils.getAppUserSettings;

import it.lbsoftware.daily.appuseractivations.AppUserActivation;
import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appuserpasswords.AppUserPasswordSecurityService;
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

/** Main app user creation service implementation. */
@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserCreationServiceImpl implements AppUserCreationService {

  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final AppUserSettingService appUserSettingService;
  private final AppUserActivationService appUserActivationService;
  private final AppUserRemovalInformationRepository appUserRemovalInformationRepository;
  private final AppUserPasswordSecurityService appUserPasswordSecurityService;

  @Override
  @Transactional
  public Optional<UUID> createDailyAppUser(@NonNull final AppUserDto appUserDto) {
    var email = appUserDto.getEmail();
    if (appUserRepository.findByEmailIgnoreCase(email).isPresent()) {
      log.warn(
          "Rejecting app user creation for e-mail %s because it is already taken".formatted(email));
      return Optional.empty();
    }
    // Password security checks
    if (!passwordSecurityChecksPass(appUserDto.getPassword())) {
      log.warn(
          "Rejecting app user creation for e-mail %s because password security checks failed"
              .formatted(email));
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

  private boolean passwordSecurityChecksPass(final String cleartextPassword) {
    try {
      appUserPasswordSecurityService.check(cleartextPassword);
      return true;
    } catch (Exception e) {
      log.warn("Password security checks failed", e);
    }
    return false;
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
   * Updates an OAuth2 {@link AppUser} with the provided data; this method assumes the {@link
   * AppUser} already existed and update data has already been validated.
   *
   * @param appUser The {@link AppUser} to update
   * @param appUserDto The new data to update the {@link AppUser} with
   */
  private void updateOauth2AppUser(final AppUser appUser, final AppUserDto appUserDto) {
    var previousEmail = appUser.getEmail();
    var newEmail = appUserDto.getEmail();
    if (!previousEmail.equals(newEmail)) {
      if (appUserRepository.findByEmailIgnoreCase(newEmail).isEmpty()) {
        appUser.setEmail(newEmail);
        log.info(
            "The OAuth2 AppUser changed e-mail address from " + previousEmail + " to " + newEmail);
      } else {
        log.warn(
            "Rejecting app user update from e-mail %s to %s because %s is already taken"
                .formatted(previousEmail, newEmail, newEmail));
        throw new IllegalArgumentException("Invalid e-mail address: " + newEmail);
      }
    }
  }

  private void createOauth2AppUser(
      final AppUserDto appUserDto, final AuthProvider authProvider, final String authProviderId) {
    var email = appUserDto.getEmail();
    if (appUserRepository.findByEmailIgnoreCase(email).isPresent()) {
      log.warn(
          "Rejecting app user creation for e-mail %s because it is already taken".formatted(email));
      throw new IllegalArgumentException("Invalid e-mail address: " + email);
    }
    var appUser = buildOauth2AppUser(appUserDto, authProvider, authProviderId);
    var savedAppUser = appUserRepository.saveAndFlush(appUser);
    // Create settings
    appUserSettingService.createAppUserSettings(getAppUserSettings(appUserDto), savedAppUser);
    // Create removal information
    appUserRemovalInformationRepository.save(
        AppUserRemovalInformation.builder().appUser(appUser).failures(0).notifiedAt(null).build());
  }
}
