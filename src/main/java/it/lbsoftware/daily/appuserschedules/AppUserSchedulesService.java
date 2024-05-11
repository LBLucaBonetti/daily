package it.lbsoftware.daily.appuserschedules;

import it.lbsoftware.daily.appuserremovers.AppUserRemoverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserSchedulesService {

  private final AppUserRemoverService appUserRemoverService;

  @Scheduled(cron = "@daily")
  public void execute() {
    log.info("Start of app user schedules service");
    appUserRemoverService.notifyForRemovalAndRemove();
    log.info("End of app user schedules service");
  }
}
