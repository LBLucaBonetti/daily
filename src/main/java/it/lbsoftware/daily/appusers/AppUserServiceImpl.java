package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import it.lbsoftware.daily.bases.BaseEntity;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserServiceImpl implements AppUserService {

  private final AppUserRepository appUserRepository;
  private final AppUserSettingService appUserSettingService;
  private final AppUserActivationService appUserActivationService;

  @Override
  public UUID getUuid(@NonNull Object principal) {
    return Optional.ofNullable(getAppUserInfo(principal).email())
        .flatMap(appUserRepository::findByEmailIgnoreCase)
        .map(BaseEntity::getUuid)
        .orElseThrow();
  }

  @Override
  public InfoDto getAppUserInfo(@NonNull Object principal) {
    String fullName;
    String email;
    if (principal instanceof OidcUser appUserOidcUser) {
      fullName = appUserOidcUser.getFullName();
      email = appUserOidcUser.getEmail();
    } else if (principal instanceof AppUserDetails appUserDetails) {
      fullName = appUserDetails.getFullname();
      email = appUserDetails.getUsername();
    } else {
      log.warn("Invalid AppUser instance detected");
      throw new IllegalStateException();
    }
    return new InfoDto(fullName, email);
  }

  @Override
  @Transactional
  public boolean activate(UUID activationCode) {
    return appUserActivationService
        .readAppUserActivation(activationCode)
        .filter(
            appUserActivation ->
                !appUserActivationService.isActivated(appUserActivation)
                    && appUserActivationService.isValid(appUserActivation))
        .map(
            appUserActivation -> {
              appUserActivationService.setActivated(appUserActivation);
              AppUser appUser = appUserActivation.getAppUser();
              appUser.setEnabled(true);
              appUserRepository.save(appUser);
              return true;
            })
        .orElse(false);
  }
}
