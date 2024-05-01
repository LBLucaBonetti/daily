package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.appusers.AppUser.AuthProvider.DAILY;
import static it.lbsoftware.daily.appusers.AppUserUtils.getAuthProvider;
import static it.lbsoftware.daily.appusers.AppUserUtils.isDailyAuthProvider;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.ACCESS_DENIED;

import it.lbsoftware.daily.appusercreations.AppUserCreationService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

  private final OidcUserService oidcUserService;
  private final AppUserCreationService appUserCreationService;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = oidcUserService.loadUser(userRequest);

    var email = validateEmail(oidcUser);
    var authProvider = getAuthProvider(email);
    if (isDailyAuthProvider(authProvider)) {
      log.error(
          "Invalid OAuth2 provider for AppUser with e-mail "
              + email
              + "; detected auth provider: "
              + DAILY);
      throw new OAuth2AuthenticationException(ACCESS_DENIED);
    }

    // The subject coming from OAuth2 is the unique key of the user in the OAuth2 provider realm
    appUserCreationService.createOrUpdateOauth2AppUser(
        getAppUserDto(email), authProvider, oidcUser.getSubject());

    return oidcUser;
  }

  private AppUserDto getAppUserDto(final String validatedEmail) {
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setEmail(validatedEmail);
    return appUserDto;
  }

  private String validateEmail(final OidcUser oidcUser) {
    return Optional.ofNullable(oidcUser.getEmail())
        .filter(StringUtils::isNotBlank)
        .orElseThrow(
            () -> {
              log.error("The OAuth2 user did not provide a valid e-mail address");
              return new OAuth2AuthenticationException(ACCESS_DENIED);
            });
  }
}
