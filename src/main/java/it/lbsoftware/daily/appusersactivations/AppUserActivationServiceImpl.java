package it.lbsoftware.daily.appusersactivations;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
    return appUserActivationRepository.findByActivationCode(activationCode);
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
}
