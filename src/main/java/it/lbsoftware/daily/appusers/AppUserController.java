package it.lbsoftware.daily.appusers;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/appusers")
class AppUserController {

  @GetMapping(value = "/info")
  public ResponseEntity<InfoDto> readInfo(@AuthenticationPrincipal OidcUser appUser) {
    final String fullName = Optional.ofNullable(appUser).map(OidcUser::getFullName).orElse("");
    final String email = Optional.ofNullable(appUser).map(OidcUser::getEmail).orElse("");
    return ResponseEntity.ok(new InfoDto(fullName, email));
  }
}
