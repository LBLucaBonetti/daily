package it.lbsoftware.daily.appuseractivations;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.config.Constants;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserActivationServiceImpl implements AppUserActivationService {

  private final AppUserActivationRepository appUserActivationRepository;

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
  public Optional<AppUserActivation> readAppUserActivation(@NonNull UUID activationCode) {
    return appUserActivationRepository.findByActivationCodeFetchAppUser(activationCode);
  }

  @Override
  public void setActivated(@NonNull AppUserActivation appUserActivation) {
    appUserActivation.setActivatedAt(LocalDateTime.now());

    appUserActivationRepository.save(appUserActivation);
  }

  @Override
  public boolean isActivated(@NonNull AppUserActivation appUserActivation) {
    return appUserActivation.getActivatedAt() != null;
  }

  @Override
  public boolean isValid(@NonNull AppUserActivation appUserActivation) {
    return Optional.ofNullable(appUserActivation.getExpiredAt())
        .map(expiredAt -> LocalDateTime.now().isBefore(expiredAt))
        .orElse(false);
  }

  private boolean isOauth2AuthProvider(AuthProvider authProvider) {
    return !AuthProvider.DAILY.equals(authProvider);
  }

  @Override
  public String getActivationUri(final String activationCode) {
    return Optional.ofNullable(activationCode)
        .filter(StringUtils::isNotBlank)
        .filter(this::isValidUuid)
        .map(
            (String appUserActivationCode) ->
                ServletUriComponentsBuilder.fromCurrentContextPath()
                    .pathSegment(Constants.ACTIVATIONS_VIEW, appUserActivationCode)
                    .build()
                    .toUriString())
        .orElseThrow(
            () -> new IllegalArgumentException("The provided activation code is malformed"));
  }

  private boolean isValidUuid(final String uuid) {
    try {
      var parsedUuid = UUID.fromString(uuid);
      log.debug("The UUID string " + parsedUuid + " is a valid UUID instance");
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
