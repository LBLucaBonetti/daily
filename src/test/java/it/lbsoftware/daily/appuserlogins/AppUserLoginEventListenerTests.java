package it.lbsoftware.daily.appuserlogins;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appuserevents.AppUserLoginEvent;
import it.lbsoftware.daily.appuserremovers.AppUserRemovalInformation;
import it.lbsoftware.daily.appuserremovers.AppUserRemovalInformationRepository;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.appusers.InfoDto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

@ExtendWith(OutputCaptureExtension.class)
class AppUserLoginEventListenerTests extends DailyAbstractUnitTests {

  @Mock private AppUserRepository appUserRepository;
  @Mock private ApplicationEventPublisher applicationEventPublisher;
  @Mock private AppUserService appUserService;
  @Mock private AppUserRemovalInformationRepository appUserRemovalInformationRepository;
  private AppUserLoginEventListener appUserLoginEventListener;

  @BeforeEach
  void beforeEach() {
    appUserLoginEventListener =
        new AppUserLoginEventListener(
            appUserRepository,
            applicationEventPublisher,
            appUserService,
            appUserRemovalInformationRepository);
  }

  @Test
  @DisplayName("Should publish an event when authentication succeeds")
  void test1() {
    // Given
    var authentication = mock(Authentication.class);
    var infoDto = new InfoDto("Full name", "user@email.com");
    given(appUserService.getAppUserInfo(any())).willReturn(infoDto);
    var event = new AuthenticationSuccessEvent(authentication);

    // When
    appUserLoginEventListener.onAuthenticationSuccess(event);

    // Then
    verify(applicationEventPublisher, times(1)).publishEvent(any(AppUserLoginEvent.class));
  }

  @Test
  @DisplayName("Should set last login on login event when AppUser is found")
  void test2() {
    // Given
    var email = "user@email.com";
    var event = new AppUserLoginEvent(email);
    var appUser = AppUser.builder().email(email).lastLoginAt(null).build();
    when(appUserRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(appUser));
    var appUserRemovalInformation = mock(AppUserRemovalInformation.class);
    when(appUserRemovalInformationRepository.findByAppUser(appUser))
        .thenReturn(Optional.of(appUserRemovalInformation));

    // When
    appUserLoginEventListener.onAppUserLoginEvent(event);

    // Then
    assertNotNull(appUser.getLastLoginAt());
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(email);
    verify(appUserRemovalInformationRepository, times(1)).findByAppUser(appUser);
  }

  @Test
  @DisplayName("Should not set last login on login event when AppUser is not found")
  void test3(final CapturedOutput capturedOutput) {
    // Given
    var email = "user@email.com";
    var event = new AppUserLoginEvent(email);
    when(appUserRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

    // When
    appUserLoginEventListener.onAppUserLoginEvent(event);

    // Then
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(email);
    assertThat(capturedOutput).contains("AppUser with e-mail " + email + " not found");
  }

  @Test
  @DisplayName(
      "Should not set last login on login event when AppUserRemovalInformation is not found")
  void test4(final CapturedOutput capturedOutput) {
    // Given
    var email = "user@email.com";
    var event = new AppUserLoginEvent(email);
    var appUser = AppUser.builder().email(email).build();
    when(appUserRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(appUser));
    when(appUserRemovalInformationRepository.findByAppUser(appUser)).thenReturn(Optional.empty());

    // When
    appUserLoginEventListener.onAppUserLoginEvent(event);

    // Then
    verify(appUserRepository, times(1)).findByEmailIgnoreCase(email);
    verify(appUserRemovalInformationRepository, times(1)).findByAppUser(appUser);
    assertThat(capturedOutput)
        .contains("AppUserRemovalInformation for AppUser with e-mail " + email + " not found");
  }
}
