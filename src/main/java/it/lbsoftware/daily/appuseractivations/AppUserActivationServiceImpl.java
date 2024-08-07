package it.lbsoftware.daily.appuseractivations;

import static it.lbsoftware.daily.appusers.AppUserUtils.isOauth2AuthProvider;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.config.DailyConfig;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

/** Main {@link AppUserActivation} service implementation. */
@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserActivationServiceImpl implements AppUserActivationService {

  private final AppUserActivationRepository appUserActivationRepository;
  private final DailyConfig dailyConfig;

  @Override
  @Transactional
  public Optional<AppUserActivation> createAppUserActivation(@NonNull AppUser appUser) {
    if (isOauth2AuthProvider(appUser.getAuthProvider())) {
      return Optional.empty();
    }
    AppUserActivation appUserActivation =
        AppUserActivation.builder()
            .appUser(appUser)
            .activationCode(UUID.randomUUID())
            .expiredAt(LocalDateTime.now().plusDays(1))
            .build();
    return Optional.of(appUserActivationRepository.saveAndFlush(appUserActivation));
  }

  @Override
  @Transactional
  public boolean setNonActivatedAndStillValidAppUserActivationActivated(
      @NonNull UUID activationCode) {
    return appUserActivationRepository
        .findNonActivatedAndStillValidAppUserActivationFetchAppUser(
            activationCode, LocalDateTime.now())
        .map(
            appUserActivation -> {
              appUserActivation.setActivatedAt(LocalDateTime.now());
              AppUser appUser = appUserActivation.getAppUser();
              appUser.setEnabled(true);
              return true;
            })
        .orElse(false);
  }

  @Override
  public String getActivationUri(final UUID activationCode) {
    return Optional.ofNullable(activationCode)
        .map(
            (UUID appUserActivationCode) ->
                UriComponentsBuilder.fromUriString(dailyConfig.getBaseUri())
                    .pathSegment(Constants.ACTIVATIONS_VIEW, appUserActivationCode.toString())
                    .build()
                    .toUriString())
        .orElseThrow(
            () -> new IllegalArgumentException("The provided activation code is malformed"));
  }
}
