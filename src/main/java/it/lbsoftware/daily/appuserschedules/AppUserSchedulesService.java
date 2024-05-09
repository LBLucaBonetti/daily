package it.lbsoftware.daily.appuserschedules;

import it.lbsoftware.daily.appuserremovers.AppUserRemoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserSchedulesService {

  private final AppUserRemoverService appUserRemoverService;

  @Scheduled(cron = "@daily")
  public void execute() {
    appUserRemoverService.notifyForRemovalAndRemove();
  }
}
