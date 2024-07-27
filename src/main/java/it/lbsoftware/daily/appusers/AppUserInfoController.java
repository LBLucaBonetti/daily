package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.config.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = Constants.APP_USER_PATH)
@CommonsLog
class AppUserInfoController {

  private final AppUserService appUserService;

  @GetMapping(value = "/info")
  public ResponseEntity<InfoDto> readInfo(@AuthenticationPrincipal Object principal) {
    log.info("GET request to /api/appusers/info");
    return ResponseEntity.ok(appUserService.getAppUserInfo(principal));
  }
}
