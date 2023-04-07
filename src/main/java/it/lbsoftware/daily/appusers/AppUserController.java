package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appusersettings.AppUserSettingDto;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
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
@RequestMapping(value = "/api/appusers")
class AppUserController {

  private final AppUserService appUserService;
  private final AppUserSettingService appUserSettingService;

  @GetMapping(value = "/info")
  public ResponseEntity<InfoDto> readInfo(@AuthenticationPrincipal Object principal) {
    return ResponseEntity.ok(appUserService.getAppUserInfo(principal));
  }

  @GetMapping(value = "/settings")
  public ResponseEntity<AppUserSettingDto> readAppUserSettings(
      @AuthenticationPrincipal Object principal) {
    return appUserSettingService
        .readAppUserSettings(appUserService.getUuid(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping(value = "/settings")
  public ResponseEntity<AppUserSettingDto> updateAppUserSetting(
      @Valid @RequestBody AppUserSettingDto appUserSetting,
      @AuthenticationPrincipal Object principal) {
    return appUserSettingService
        .updateAppUserSettings(appUserSetting, appUserService.getUuid(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
