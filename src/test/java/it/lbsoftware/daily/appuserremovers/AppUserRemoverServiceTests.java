package it.lbsoftware.daily.appuserremovers;

import static it.lbsoftware.daily.config.Constants.FAILURES_THRESHOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appuseractivations.AppUserActivationRepository;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusersettings.AppUserSettingRepository;
import it.lbsoftware.daily.config.DailyConfig;
import it.lbsoftware.daily.emails.EmailService;
import it.lbsoftware.daily.exceptions.DailyEmailException;
import it.lbsoftware.daily.notes.NoteRepository;
import it.lbsoftware.daily.tags.TagRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class AppUserRemoverServiceTests extends DailyAbstractUnitTests {

  @Mock private TagRepository tagRepository;
  @Mock private NoteRepository noteRepository;
  @Mock private AppUserActivationRepository appUserActivationRepository;
  @Mock private AppUserSettingRepository appUserSettingRepository;
  @Mock private AppUserRemovalInformationRepository appUserRemovalInformationRepository;
  @Mock private AppUserRepository appUserRepository;
  @Mock private EmailService emailService;
  @Mock private DailyConfig dailyConfig;
  private AppUserRemoverService appUserRemoverService;

  @BeforeEach
  void beforeEach() {
    this.appUserRemoverService =
        new AppUserRemoverService(
            tagRepository,
            noteRepository,
            appUserActivationRepository,
            appUserSettingRepository,
            appUserRemovalInformationRepository,
            appUserRepository,
            emailService,
            dailyConfig);
  }

  @Test
  @DisplayName("Should not throw when creating map to send removal notification e-mail")
  void test1() {
    // Given
    var appUser = AppUser.builder().firstName(null).build();
    given(appUserRepository.findToNotifyForRemoval(any())).willReturn(Set.of(appUser));
    var appUserRemovalInformation = AppUserRemovalInformation.builder().appUser(appUser).build();
    given(appUserRemovalInformationRepository.findByAppUser(appUser))
        .willReturn(Optional.of(appUserRemovalInformation));
    given(dailyConfig.getBaseUri()).willReturn("baseUri");

    // When and then
    assertDoesNotThrow(() -> appUserRemoverService.notifyForRemovalAndRemove());
  }

  @Test
  @DisplayName("Should log and return when AppUserRemovalInformation could not be found")
  void test2(final CapturedOutput capturedOutput) {
    // Given
    var email = "appuser@email.com";
    var appUser = AppUser.builder().email(email).firstName("First name").build();
    given(appUserRepository.findToNotifyForRemoval(any())).willReturn(Set.of(appUser));
    given(appUserRemovalInformationRepository.findByAppUser(appUser)).willReturn(Optional.empty());

    // When
    appUserRemoverService.notifyForRemovalAndRemove();

    // Then
    assertThat(capturedOutput)
        .contains("Could not find AppUserRemovalInformation for AppUser with e-mail " + email);
    verify(emailService, times(0)).sendAsynchronously(any(), any());
  }

  @Test
  @DisplayName("Should increment failures when notification e-mail could not be sent")
  void test3() {
    // Given
    var email = "appuser@email.com";
    var appUser = AppUser.builder().email(email).firstName("First name").build();
    given(appUserRepository.findToNotifyForRemoval(any())).willReturn(Set.of(appUser));
    var appUserRemovalInformation = mock(AppUserRemovalInformation.class);
    given(appUserRemovalInformationRepository.findByAppUser(appUser))
        .willReturn(Optional.of(appUserRemovalInformation));
    given(dailyConfig.getBaseUri()).willReturn("http://localhost:8080");
    doThrow(new DailyEmailException()).when(emailService).sendSynchronously(any(), any());
    assertEquals(0, appUserRemovalInformation.getFailures());
    assertNull(appUserRemovalInformation.getNotifiedAt());

    // When
    appUserRemoverService.notifyForRemovalAndRemove();

    // Then
    verify(emailService, times(1)).sendSynchronously(any(), any());
    verify(appUserRemovalInformation, times(1)).setFailures(1);
    verify(appUserRemovalInformation, times(0)).setNotifiedAt(any());
  }

  @Test
  @DisplayName(
      "Should set AppUser as notified for removal when failures are greater than threshold")
  void test4() {
    // Given
    var email = "appuser@email.com";
    var appUser = AppUser.builder().email(email).firstName("First name").build();
    given(appUserRepository.findToNotifyForRemoval(any())).willReturn(Set.of(appUser));
    var appUserRemovalInformation =
        AppUserRemovalInformation.builder()
            .notifiedAt(null)
            .failures(FAILURES_THRESHOLD - 1)
            .build();
    given(appUserRemovalInformationRepository.findByAppUser(appUser))
        .willReturn(Optional.of(appUserRemovalInformation));
    given(dailyConfig.getBaseUri()).willReturn("http://localhost:8080");
    doThrow(new DailyEmailException()).when(emailService).sendSynchronously(any(), any());
    assertEquals(FAILURES_THRESHOLD - 1, appUserRemovalInformation.getFailures());
    assertNull(appUserRemovalInformation.getNotifiedAt());

    // When
    appUserRemoverService.notifyForRemovalAndRemove();

    // Then
    verify(emailService, times(1)).sendSynchronously(any(), any());
    assertEquals(FAILURES_THRESHOLD, appUserRemovalInformation.getFailures());
    assertNotNull(appUserRemovalInformation.getNotifiedAt());
  }

  @Test
  @DisplayName("Should log when AppUser cannot be removed")
  void test5(final CapturedOutput capturedOutput) {
    // Given
    var email = "appuser@email.com";
    var appUser = AppUser.builder().email(email).firstName("First name").build();
    given(appUserRepository.findToRemove(any(), any())).willReturn(Set.of(appUser));
    doThrow(RuntimeException.class).when(appUserRepository).delete(appUser);

    // When
    appUserRemoverService.notifyForRemovalAndRemove();

    // Then
    assertThat(capturedOutput)
        .contains(
            "The AppUser with e-mail "
                + email
                + " could not be removed; another attempt will be made next time");
  }
}
