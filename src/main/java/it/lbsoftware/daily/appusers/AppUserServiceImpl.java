package it.lbsoftware.daily.appusers;

import java.util.Optional;
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
  @Transactional(readOnly = true)
  public AppUser getAppUser(@NonNull final Object principal) {
    return Optional.ofNullable(getAppUserInfo(principal).email())
        .flatMap(appUserRepository::findByEmailIgnoreCase)
        .orElseThrow();
  }
}
