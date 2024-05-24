package it.lbsoftware.daily.appuserschedules;

import it.lbsoftware.daily.appuserremovers.AppUserRemoverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/** Scheduled services dealing with {@link it.lbsoftware.daily.appusers.AppUser} entities. */
@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserSchedulesService {

  private final AppUserRemoverService appUserRemoverService;

  /** Scheduled task for {@link it.lbsoftware.daily.appusers.AppUser} entities. */
  @Scheduled(cron = "@daily")
  public void execute() {
    log.info("Start of app user schedules service");
    appUserRemoverService.notifyForRemovalAndRemove();
    log.info("End of app user schedules service");
  }
}
