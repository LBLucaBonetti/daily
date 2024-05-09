package it.lbsoftware.daily.appuserlogins;

import it.lbsoftware.daily.appuserevents.AppUserLoginEvent;
import it.lbsoftware.daily.appuserremovers.AppUserRemovalInformation;
import it.lbsoftware.daily.appuserremovers.AppUserRemovalInformationRepository;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@CommonsLog
public class AppUserLoginEventListener {

  private final AppUserRepository appUserRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final AppUserService appUserService;
  private final AppUserRemovalInformationRepository appUserRemovalInformationRepository;

  /**
   * This method is triggered by {@code DefaultAuthenticationEventPublisher} whenever an {@code
   * AppUser} logs in
   */
  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent authenticationSuccessEvent) {
    applicationEventPublisher.publishEvent(
        new AppUserLoginEvent(
            appUserService
                .getAppUserInfo(authenticationSuccessEvent.getAuthentication().getPrincipal())
                .email()));
  }

  /**
   * This method is triggered whenever an {@code AppUserLoginEvent} is published
   *
   * @param loginEvent The login event that triggered this method call
   */
  @EventListener
  @Transactional
  public void onAppUserLoginEvent(AppUserLoginEvent loginEvent) {
    var email = loginEvent.email();
    log.info("Login of AppUser with e-mail " + email);
    appUserRepository
        .findByEmailIgnoreCase(email)
        .ifPresentOrElse(
            (AppUser appUser) -> {
              appUser.setLastLoginAt(LocalDateTime.now());
              // Reset removal information since the app user logged in, and it could have already
              // been marked for removal and been notified about it
              appUserRemovalInformationRepository
                  .findByAppUser(appUser)
                  .ifPresentOrElse(
                      (AppUserRemovalInformation appUserRemovalInformation) -> {
                        appUserRemovalInformation.setFailures(0);
                        appUserRemovalInformation.setNotifiedAt(null);
                      },
                      () ->
                          log.warn(
                              "AppUserRemovalInformation for AppUser with e-mail "
                                  + email
                                  + " not found"));
            },
            () -> log.warn("AppUser with e-mail " + email + " not found"));
  }
}
