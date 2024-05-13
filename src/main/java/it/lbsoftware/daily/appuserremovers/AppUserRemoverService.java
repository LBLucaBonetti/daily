package it.lbsoftware.daily.appuserremovers;

import static it.lbsoftware.daily.config.Constants.FAILURES_THRESHOLD;
import static it.lbsoftware.daily.config.Constants.REMOVAL_NOTIFICATION_THRESHOLD_DAYS;
import static it.lbsoftware.daily.config.Constants.REMOVAL_NOTIFICATION_TO_REMOVAL_DELTA_DAYS;
import static it.lbsoftware.daily.config.Constants.REMOVAL_THRESHOLD_DAYS;

import it.lbsoftware.daily.appuseractivations.AppUserActivationRepository;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserUtils;
import it.lbsoftware.daily.appusersettings.AppUserSettingRepository;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.config.DailyConfig;
import it.lbsoftware.daily.emails.EmailInfo;
import it.lbsoftware.daily.emails.EmailService;
import it.lbsoftware.daily.exceptions.DailyEmailException;
import it.lbsoftware.daily.notes.NoteRepository;
import it.lbsoftware.daily.tags.TagRepository;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserRemoverService {

  private final TagRepository tagRepository;
  private final NoteRepository noteRepository;
  private final AppUserActivationRepository appUserActivationRepository;
  private final AppUserSettingRepository appUserSettingRepository;
  private final AppUserRemovalInformationRepository appUserRemovalInformationRepository;
  private final AppUserRepository appUserRepository;
  private final EmailService emailService;
  private final DailyConfig dailyConfig;

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void notifyForRemovalAndRemove() {
    var now = LocalDateTime.now();
    notifyForRemoval(now);
    remove(now);
  }

  private void notifyForRemoval(final LocalDateTime now) {
    var removalNotificationThreshold = now.minusDays(REMOVAL_NOTIFICATION_THRESHOLD_DAYS);
    var appUsersToNotifyForRemoval =
        appUserRepository.findToNotifyForRemoval(removalNotificationThreshold);
    log.info(
        "Found %s AppUser records to notify for removal"
            .formatted(appUsersToNotifyForRemoval.size()));
    appUsersToNotifyForRemoval.forEach(this::notifyForRemoval);
  }

  private void notifyForRemoval(final AppUser appUser) {
    var email = appUser.getEmail();
    var appUserRemovalInformationOptional =
        appUserRemovalInformationRepository.findByAppUser(appUser);
    AppUserRemovalInformation appUserRemovalInformation;
    if (appUserRemovalInformationOptional.isPresent()) {
      appUserRemovalInformation = appUserRemovalInformationOptional.get();
    } else {
      log.error("Could not find AppUserRemovalInformation for AppUser with e-mail " + email);
      return;
    }
    log.info(
        "Trying to notify for removal AppUser with e-mail "
            + email
            + "; attempt #"
            + (appUserRemovalInformation.getFailures() + 1));
    try {
      emailService.sendSynchronously(
          new EmailInfo(
              Constants.EMAIL_APP_USER_REMOVAL_NOTIFICATION_PATH,
              email,
              Constants.EMAIL_APP_USER_REMOVAL_NOTIFICATION_SUBJECT),
          Map.of(
              "appUserFirstName",
              AppUserUtils.getFirstNameOrDefault(appUser),
              "hoursBeforeRemoval",
              REMOVAL_NOTIFICATION_TO_REMOVAL_DELTA_DAYS * 24,
              "baseUri",
              dailyConfig.getBaseUri()));
      appUserRemovalInformation.setNotifiedAt(LocalDateTime.now());
      log.info(
          "The AppUser with e-mail "
              + email
              + " has been successfully notified for removal from daily");
    } catch (DailyEmailException e) {
      appUserRemovalInformation.setFailures(appUserRemovalInformation.getFailures() + 1);
      if (appUserRemovalInformation.getFailures() >= FAILURES_THRESHOLD) {
        appUserRemovalInformation.setNotifiedAt(LocalDateTime.now());
        log.warn(
            "The number of failures trying to notify the AppUser with e-mail "
                + email
                + " has reached its limit; the AppUser will be marked for removal anyway");
      } else {
        log.error(
            "The AppUser with e-mail "
                + email
                + " could not be notified for removal; another attempt will be made next time");
      }
    }
    appUserRemovalInformationRepository.saveAndFlush(appUserRemovalInformation);
  }

  private void remove(final LocalDateTime now) {
    var removalThreshold = now.minusDays(REMOVAL_THRESHOLD_DAYS);
    var removalNotificationThreshold = now.minusDays(REMOVAL_NOTIFICATION_TO_REMOVAL_DELTA_DAYS);
    var appUsersToRemove =
        appUserRepository.findToRemove(removalThreshold, removalNotificationThreshold);
    log.info("Found %s AppUser records to remove".formatted(appUsersToRemove.size()));
    appUsersToRemove.forEach(this::remove);
  }

  private void remove(final AppUser appUser) {
    var email = appUser.getEmail();
    log.info("Trying to remove AppUser with e-mail " + email);
    try {
      // Remove tags
      tagRepository.deleteByAppUser(appUser);
      // Remove notes
      noteRepository.deleteByAppUser(appUser);
      // Remove activation
      appUserActivationRepository.deleteByAppUser(appUser);
      // Remove settings
      appUserSettingRepository.deleteByAppUser(appUser);
      // Remove removal information
      appUserRemovalInformationRepository.deleteByAppUser(appUser);
      // Remove app user
      appUserRepository.delete(appUser);
      log.info("The AppUser with e-mail " + email + " has been successfully removed from daily");
    } catch (Exception e) {
      log.error(
          "The AppUser with e-mail "
              + email
              + " could not be removed; another attempt will be made next time");
    }
  }
}
