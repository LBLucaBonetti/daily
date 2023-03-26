package it.lbsoftware.daily.appusers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/appusers")
class AppUserController {

  private final AppUserService appUserService;

  @GetMapping(value = "/info")
  public ResponseEntity<InfoDto> readInfo(@AuthenticationPrincipal Object principal) {
    return ResponseEntity.ok(appUserService.getAppUserInfo(principal));
  }
}
