package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.appusers.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
class AppUserSettingController {

  private final AppUserService appUserService;
  private final AppUserSettingService appUserSettingService;

  @GetMapping
  public ResponseEntity<AppUserSettingDto> readAppUserSettings(
      @AuthenticationPrincipal Object principal) {
    return appUserSettingService
        .readAppUserSettings(appUserService.getUuid(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping
  public ResponseEntity<AppUserSettingDto> updateAppUserSettings(
      @Valid @RequestBody AppUserSettingDto appUserSettings,
      @AuthenticationPrincipal Object principal) {
    return appUserSettingService
        .updateAppUserSettings(appUserSettings, appUserService.getUuid(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
