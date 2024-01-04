package it.lbsoftware.daily.appusersignups;

import static it.lbsoftware.daily.config.Constants.SIGNUP_SUCCESS;
import static it.lbsoftware.daily.templates.TemplateUtils.addErrorToView;
import static it.lbsoftware.daily.templates.TemplateUtils.getOauth2AuthProvider;

import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appusercreations.AppUserCreationService;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.emails.EmailInfo;
import it.lbsoftware.daily.emails.EmailService;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
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
    String oauth2Provider = getOauth2AuthProvider(appUserDto.getEmail());
    if (StringUtils.isNotBlank(oauth2Provider)) {
      addErrorToView(
          bindingResult,
          "You are not allowed to sign up with the provided email address. Go back to the login page and use the "
              + oauth2Provider
              + " link to log in");
      return;
    }
    // 4. If the e-mail is not taken, creation can be performed
    appUserCreationService
        .createDailyAppUser(appUserDto)
        .ifPresentOrElse(
            (String activationCode) -> {
              // Success, send the activation e-mail
              model.addAttribute(SIGNUP_SUCCESS, "Check your email to activate your account");
              // The following call to ServletUriComponentsBuilder.fromCurrentContextPath() is not
              // meant to be used without an active HttpServletRequest (i.e.: from an @Async method)
              emailService.send(
                  new EmailInfo(
                      Constants.EMAIL_APP_USER_ACTIVATION_PATH,
                      appUserDto.getEmail(),
                      Constants.EMAIL_APP_USER_ACTIVATION_SUBJECT),
                  Map.of(
                      "appUserFirstName",
                      appUserDto.getFirstName(),
                      "activationUri",
                      appUserActivationService.getActivationUri(activationCode)));
            },
            () ->
                // Failure, notify
                addErrorToView(bindingResult, "Invalid email or password"));
  }
}
