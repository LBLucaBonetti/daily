package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.appusers.AppUser.AuthProvider.DAILY;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.ACCESS_DENIED;

import it.lbsoftware.daily.appusercreations.AppUserCreationService;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/** A {@link OAuth2UserService} custom implementation to handle {@link AppUser} entities. */
@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

  private final OidcUserService oidcUserService;
  private final AppUserCreationService appUserCreationService;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = oidcUserService.loadUser(userRequest);

    var email = validateEmailOrThrow(oidcUser);
    var authProvider = getOauth2AuthProviderOrThrow(userRequest, email);

    // The subject coming from the OAuth2 request is the unique key of the user in the OAuth2
    // provider realm
    createOrUpdateOauth2AppUserOrThrow(getAppUserDto(email), authProvider, oidcUser.getSubject());

    return oidcUser;
  }

  private void createOrUpdateOauth2AppUserOrThrow(
      final AppUserDto appUserDto, final AuthProvider authProvider, final String authProviderId) {
    try {
      appUserCreationService.createOrUpdateOauth2AppUser(appUserDto, authProvider, authProviderId);
    } catch (Exception e) {
      log.error("Could not create or update app user with e-mail " + appUserDto.getEmail());
      throw new OAuth2AuthenticationException(ACCESS_DENIED);
    }
  }

  /**
   * Retrieves the {@code AuthProvider} from the provided request.
   *
   * @param userRequest The source request to retrieve the {@code AuthProvider} from
   * @return The specific {@code AuthProvider}
   * @throws OAuth2AuthenticationException If the {@code AuthProvider} cannot be found or is not a
   *     valid OAuth2 provider
   */
  private AuthProvider getOauth2AuthProviderOrThrow(
      final OAuth2UserRequest userRequest, final String validatedEmail) {
    return Optional.ofNullable(userRequest)
        .map(OAuth2UserRequest::getClientRegistration)
        .map(ClientRegistration::getRegistrationId)
        .filter(StringUtils::isNotBlank)
        .map(this::getAuthProvider)
        .filter(authProvider -> !DAILY.equals(authProvider))
        .orElseThrow(
            () -> {
              log.error("Invalid OAuth2 auth provider for AppUser with e-mail " + validatedEmail);
              return new OAuth2AuthenticationException(ACCESS_DENIED);
            });
  }

  /**
   * Attempts to find the valid {@code AuthProvider} from the supplied registration id.
   *
   * @param registrationId The source registration id
   * @return The corresponding {@code AuthProvider} or null if not found
   */
  private AuthProvider getAuthProvider(final String registrationId) {
    try {
      return AuthProvider.valueOf(StringUtils.toRootUpperCase(registrationId));
    } catch (NullPointerException | IllegalArgumentException e) {
      log.error(
          "Cannot convert the registration id %s to a valid auth provider"
              .formatted(registrationId),
          e);
    }
    return null;
  }

  private AppUserDto getAppUserDto(final String validatedEmail) {
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setEmail(validatedEmail);
    return appUserDto;
  }

  private String validateEmailOrThrow(final OidcUser oidcUser) {
    return Optional.ofNullable(oidcUser.getEmail())
        .filter(StringUtils::isNotBlank)
        .orElseThrow(
            () -> {
              log.error("The OAuth2 user did not provide a valid e-mail address");
              return new OAuth2AuthenticationException(ACCESS_DENIED);
            });
  }
}
