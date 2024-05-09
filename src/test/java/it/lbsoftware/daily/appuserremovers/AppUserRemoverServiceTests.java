package it.lbsoftware.daily.appuserremovers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appuseractivations.AppUserActivationRepository;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusersettings.AppUserSettingRepository;
import it.lbsoftware.daily.emails.EmailService;
import it.lbsoftware.daily.notes.NoteRepository;
import it.lbsoftware.daily.tags.TagRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class AppUserRemoverServiceTests extends DailyAbstractUnitTests {

  @Mock private TagRepository tagRepository;
  @Mock private NoteRepository noteRepository;
  @Mock private AppUserActivationRepository appUserActivationRepository;
  @Mock private AppUserSettingRepository appUserSettingRepository;
  @Mock private AppUserRemovalInformationRepository appUserRemovalInformationRepository;
  @Mock private AppUserRepository appUserRepository;
  @Mock private EmailService emailService;
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
            emailService);
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

    // When and then
    assertDoesNotThrow(() -> appUserRemoverService.notifyForRemovalAndRemove());
  }
}
