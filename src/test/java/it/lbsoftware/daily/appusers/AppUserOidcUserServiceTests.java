package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.appusers.AppUser.AuthProvider.GOOGLE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusercreations.AppUserCreationService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class AppUserOidcUserServiceTests extends DailyAbstractUnitTests {

  @Mock private OidcUserService oidcUserService;
  @Mock private AppUserCreationService appUserCreationService;
  private AppUserOidcUserService appUserOidcUserService;

  @BeforeEach
  void beforeEach() {
    appUserOidcUserService = new AppUserOidcUserService(oidcUserService, appUserCreationService);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", " "})
  @DisplayName("Should not load user because of invalid e-mail and throw")
  void test1(final String email) {
    // Given
    var userRequest = mock(OidcUserRequest.class);
    var oidcUser = mock(OidcUser.class);
    given(oidcUserService.loadUser(userRequest)).willReturn(oidcUser);
    given(oidcUser.getEmail()).willReturn(email);

    // When
    var res =
        assertThrows(
            OAuth2AuthenticationException.class,
            () -> appUserOidcUserService.loadUser(userRequest));

    // Then
    assertNotNull(res);
    verify(appUserCreationService, times(0)).createDailyAppUser(any());
    verify(appUserCreationService, times(0)).createOrUpdateOauth2AppUser(any(), any(), any());
  }

  @Test
  @DisplayName("Should not load user because of invalid (but non-DAILY) auth provider and throw")
  void test2() {
    // Given
    var email = "appuser@invalid.com";
    var userRequest = mock(OidcUserRequest.class);
    var oidcUser = mock(OidcUser.class);
    var clientRegistration = mock(ClientRegistration.class);
    var registrationId = "invalid";

    given(oidcUserService.loadUser(userRequest)).willReturn(oidcUser);
    given(oidcUser.getEmail()).willReturn(email);
    given(userRequest.getClientRegistration()).willReturn(clientRegistration);
    given(clientRegistration.getRegistrationId()).willReturn(registrationId);

    // When
    var res =
        assertThrows(
            OAuth2AuthenticationException.class,
            () -> appUserOidcUserService.loadUser(userRequest));

    // Then
    assertNotNull(res);
    verify(appUserCreationService, times(0)).createDailyAppUser(any());
    verify(appUserCreationService, times(0)).createOrUpdateOauth2AppUser(any(), any(), any());
  }

  @Test
  @DisplayName("Should not load user because of invalid (DAILY) auth provider and throw")
  void test3() {
    // Given
    var email = "appuser@daily.com";
    var userRequest = mock(OidcUserRequest.class);
    var oidcUser = mock(OidcUser.class);
    var clientRegistration = mock(ClientRegistration.class);
    var registrationId = "daily";

    given(oidcUserService.loadUser(userRequest)).willReturn(oidcUser);
    given(oidcUser.getEmail()).willReturn(email);
    given(userRequest.getClientRegistration()).willReturn(clientRegistration);
    given(clientRegistration.getRegistrationId()).willReturn(registrationId);

    // When
    var res =
        assertThrows(
            OAuth2AuthenticationException.class,
            () -> appUserOidcUserService.loadUser(userRequest));

    // Then
    assertNotNull(res);
    verify(appUserCreationService, times(0)).createDailyAppUser(any());
    verify(appUserCreationService, times(0)).createOrUpdateOauth2AppUser(any(), any(), any());
  }

  @Test
  @DisplayName("Should not load user because of error saving or updating the entity and throw")
  void test4() {
    // Given
    var email = "appuser@google.com";
    var uuid = UUID.randomUUID().toString();
    var userRequest = mock(OidcUserRequest.class);
    var oidcUser = mock(OidcUser.class);
    var clientRegistration = mock(ClientRegistration.class);
    var registrationId = "google";

    given(oidcUserService.loadUser(userRequest)).willReturn(oidcUser);
    given(oidcUser.getEmail()).willReturn(email);
    given(userRequest.getClientRegistration()).willReturn(clientRegistration);
    given(clientRegistration.getRegistrationId()).willReturn(registrationId);
    given(oidcUser.getSubject()).willReturn(uuid);
    doThrow(new RuntimeException())
        .when(appUserCreationService)
        .createOrUpdateOauth2AppUser(any(), eq(GOOGLE), eq(uuid));

    // When
    var res =
        assertThrows(
            OAuth2AuthenticationException.class,
            () -> appUserOidcUserService.loadUser(userRequest));

    // Then
    assertNotNull(res);
    verify(appUserCreationService, times(0)).createDailyAppUser(any());
    verify(appUserCreationService, times(1))
        .createOrUpdateOauth2AppUser(any(), eq(GOOGLE), eq(uuid));
  }

  @Test
  @DisplayName("Should load user and return it")
  void test5() {
    // Given
    var email = "appuser@gmail.com";
    var uuid = UUID.randomUUID().toString();
    var userRequest = mock(OidcUserRequest.class);
    var oidcUser = mock(OidcUser.class);
    var clientRegistration = mock(ClientRegistration.class);
    var registrationId = "google";

    given(oidcUserService.loadUser(userRequest)).willReturn(oidcUser);
    given(oidcUser.getEmail()).willReturn(email);
    given(userRequest.getClientRegistration()).willReturn(clientRegistration);
    given(clientRegistration.getRegistrationId()).willReturn(registrationId);
    given(oidcUser.getSubject()).willReturn(uuid);

    // When
    var res = appUserOidcUserService.loadUser(userRequest);

    // Then
    assertNotNull(res);
    verify(appUserCreationService, times(0)).createDailyAppUser(any());
    verify(appUserCreationService, times(1))
        .createOrUpdateOauth2AppUser(any(), eq(GOOGLE), eq(uuid));
  }
}
