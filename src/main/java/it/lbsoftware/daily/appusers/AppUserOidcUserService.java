package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appusercreations.AppUserCreationService;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserOidcUserService extends OidcUserService {

  private final AppUserCreationService appUserCreationService;
  private final AppUserRepository appUserRepository;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = super.loadUser(userRequest);

    final String email =
        Optional.ofNullable(oidcUser.getEmail())
            .filter(StringUtils::isNotBlank)
            .orElseThrow(
                () ->
                    new OAuth2AuthenticationException(
                        "The OAuth2 user did not provide an e-mail address"));
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setEmail(email);
    // The subject coming from OAuth2 is the unique key of the user in the OAuth2 provider realm
    appUserRepository
        .findByAuthProviderIdAndAuthProvider(oidcUser.getSubject(), AuthProvider.GOOGLE)
        .ifPresentOrElse(
            appUser -> {
              log.info("Login of the already present OAuth2 AppUser " + email);
              final String previousEmail = appUser.getEmail();
              if (!email.equals(previousEmail)) {
                appUser.setEmail(email);
                appUserRepository.save(appUser);
                log.info(
                    "The OAuth2 AppUser user has changed e-mail address from "
                        + previousEmail
                        + " to "
                        + email);
              }
            },
            () -> {
              appUserCreationService.createOauth2AppUser(
                  appUserDto, AuthProvider.GOOGLE, oidcUser.getSubject());
              log.info("Login of a new OAuth2 AppUser " + email);
            });

    return oidcUser;
  }
}
