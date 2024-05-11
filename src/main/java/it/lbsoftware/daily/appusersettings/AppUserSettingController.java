package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.appusers.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/appusers/settings")
@CommonsLog
class AppUserSettingController {

  private final AppUserService appUserService;
  private final AppUserSettingService appUserSettingService;

  @GetMapping
  public ResponseEntity<AppUserSettingDto> readAppUserSettings(
      @AuthenticationPrincipal Object principal) {
    log.info("GET request to /api/appusers/settings");
    return appUserSettingService
        .readAppUserSettings(appUserService.getAppUser(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping
  public ResponseEntity<AppUserSettingDto> updateAppUserSettings(
      @Valid @RequestBody AppUserSettingDto appUserSettings,
      @AuthenticationPrincipal Object principal) {
    log.info(
        "PUT request to /api/appusers/settings; parameters: %s"
            .formatted(appUserSettings.toString()));
    return appUserSettingService
        .updateAppUserSettings(appUserSettings, appUserService.getAppUser(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
