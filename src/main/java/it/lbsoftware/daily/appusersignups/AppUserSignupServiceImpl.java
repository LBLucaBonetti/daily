package it.lbsoftware.daily.appusersignups;

import static it.lbsoftware.daily.appusers.AppUser.AuthProvider.DAILY;
import static it.lbsoftware.daily.appusers.AppUserUtils.getOauth2AuthProvider;
import static it.lbsoftware.daily.config.Constants.SIGNUP_SUCCESS;
import static it.lbsoftware.daily.templates.TemplateUtils.addErrorToView;

import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appusercreations.AppUserCreationService;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.emails.EmailInfo;
import it.lbsoftware.daily.emails.EmailService;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserSignupServiceImpl implements AppUserSignupService {

  private final AppUserCreationService appUserCreationService;
  private final EmailService emailService;
  private final AppUserActivationService appUserActivationService;

  @Override
  public void signup(
      @NonNull AppUserDto appUserDto, @NonNull BindingResult bindingResult, @NonNull Model model) {
    // 1. Show validation errors if any
    if (bindingResult.hasErrors()) {
      return;
    }
    // 2. Passwords should match
    if (!Objects.equals(appUserDto.getPassword(), appUserDto.getPasswordConfirmation())) {
      addErrorToView(bindingResult, "Passwords should match");
      return;
    }
    // 3. E-mail should not be OAuth2 (users should use the login screen link to perform login with
    // their OAuth2 e-mail address)
    var oauth2Provider = getOauth2AuthProvider(appUserDto.getEmail());
    if (DAILY != oauth2Provider) {
      addErrorToView(
          bindingResult,
          "You are not allowed to sign up with the provided e-mail address. Go back to the login page and use the "
              + oauth2Provider
              + " link to log in");
      return;
    }
    // 4. If the e-mail is not taken, creation can be performed
    appUserCreationService
        .createDailyAppUser(appUserDto)
        .ifPresentOrElse(
            (UUID activationCode) -> {
              // Success, send the activation e-mail
              model.addAttribute(SIGNUP_SUCCESS, "Check your email to activate your account");
              emailService.send(
                  new EmailInfo(
                      Constants.EMAIL_APP_USER_ACTIVATION_PATH,
                      appUserDto.getEmail(),
                      Constants.EMAIL_APP_USER_ACTIVATION_SUBJECT),
                  Map.of(
                      "appUserFirstName",
                      appUserDto.getFirstName(),
                      // We generate the whole URI here instead of building it with the template
                      // engine because it will be simpler to change it without touching the html
                      // file
                      "activationUri",
                      appUserActivationService.getActivationUri(activationCode)));
            },
            () ->
                // Failure, notify
                addErrorToView(bindingResult, "Invalid email or password"));
  }
}
