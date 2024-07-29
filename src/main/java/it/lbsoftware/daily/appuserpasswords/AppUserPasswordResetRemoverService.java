package it.lbsoftware.daily.appuserpasswords;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/** Deals with {@link AppUserPasswordReset} removal. */
@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserPasswordResetRemoverService {

  private final AppUserPasswordResetRepository appUserPasswordResetRepository;

  /** Finds {@link AppUserPasswordReset} entities that should be removed and removes them. */
  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void removeExpiredAndUsed() {
    var now = LocalDateTime.now();
    remove(now);
  }

  private void remove(final LocalDateTime now) {
    var appUserPasswordResetsToRemove = appUserPasswordResetRepository.findToRemove(now);
    log.info(
        "Found %s AppUserPasswordReset records to remove"
            .formatted(appUserPasswordResetsToRemove.size()));
    appUserPasswordResetsToRemove.forEach(this::remove);
  }

  private void remove(final AppUserPasswordReset appUserPasswordReset) {
    var passwordResetCode = appUserPasswordReset.getPasswordResetCode();
    log.info("Trying to remove AppUserPasswordReset with password reset code " + passwordResetCode);
    try {
      appUserPasswordResetRepository.delete(appUserPasswordReset);
      log.info(
          "The AppUserPasswordReset with password reset code "
              + passwordResetCode
              + " has been successfully removed from daily");
    } catch (Exception e) {
      log.error(
          "The AppUserPasswordReset with password reset code "
              + passwordResetCode
              + " could not be removed");
    }
  }
}
