package it.lbsoftware.daily.appuserpasswords;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class AppUserPasswordResetRemoverServiceTests extends DailyAbstractUnitTests {

  @Mock private AppUserPasswordResetRepository appUserPasswordResetRepository;
  private AppUserPasswordResetRemoverService appUserPasswordResetRemoverService;

  @BeforeEach
  void beforeEach() {
    this.appUserPasswordResetRemoverService =
        new AppUserPasswordResetRemoverService(appUserPasswordResetRepository);
  }

  @Test
  @DisplayName("Should log when AppUserPasswordReset cannot be removed")
  void test1(final CapturedOutput capturedOutput) {
    // Given
    var passwordResetCode = UUID.randomUUID();
    var appUserPasswordReset =
        AppUserPasswordReset.builder().passwordResetCode(passwordResetCode).build();
    given(appUserPasswordResetRepository.findToRemove(any()))
        .willReturn(Set.of(appUserPasswordReset));
    doThrow(RuntimeException.class)
        .when(appUserPasswordResetRepository)
        .delete(appUserPasswordReset);

    // When
    appUserPasswordResetRemoverService.removeExpiredAndUsed();

    // Then
    assertThat(capturedOutput)
        .contains(
            "The AppUserPasswordReset with password reset code "
                + passwordResetCode
                + " could not be removed");
  }

  @Test
  @DisplayName("Should log when AppUserPasswordReset is removed")
  void test3(final CapturedOutput capturedOutput) {
    // Given
    var passwordResetCode = UUID.randomUUID();
    var appUserPasswordReset =
        AppUserPasswordReset.builder().passwordResetCode(passwordResetCode).build();
    given(appUserPasswordResetRepository.findToRemove(any()))
        .willReturn(Set.of(appUserPasswordReset));

    // When
    appUserPasswordResetRemoverService.removeExpiredAndUsed();

    // Then
    assertThat(capturedOutput)
        .contains(
            "The AppUserPasswordReset with password reset code "
                + passwordResetCode
                + " has been successfully removed from daily");
  }
}
