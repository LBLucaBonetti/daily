package it.lbsoftware.daily.appusersignups;

import static it.lbsoftware.daily.appusers.AppUserUtils.getAuthProvider;
import static it.lbsoftware.daily.appusers.AppUserUtils.isOauth2AuthProvider;
import static it.lbsoftware.daily.config.Constants.SIGNUP_SUCCESS;
import static it.lbsoftware.daily.templates.TemplateUtils.addErrorToView;

import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appusercreations.AppUserCreationService;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.emails.EmailInfo;
import it.lbsoftware.daily.emails.EmailService;
import it.lbsoftware.daily.exceptions.DailyEmailException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
    var oauth2Provider = getAuthProvider(appUserDto.getEmail());
    if (isOauth2AuthProvider(oauth2Provider)) {
      addErrorToView(
          bindingResult,
          "You are not allowed to sign up with the provided e-mail address. Go back to the login page and use the "
              + StringUtils.capitalize(StringUtils.toRootLowerCase(oauth2Provider.toString()))
              + " link to log in");
      return;
    }
    // 4. If the e-mail is not taken, creation can be performed
    appUserCreationService
        .createDailyAppUser(appUserDto)
        .ifPresentOrElse(
            (UUID activationCode) -> {
              // Success, try to send the activation e-mail
              if (sendActivationEmail(appUserDto, activationCode)) {
                model.addAttribute(SIGNUP_SUCCESS, "Check your e-mail to activate your account");
              } else {
                addErrorToView(
                    bindingResult, "Problems sending the activation e-mail; try again later");
              }
            },
            () ->
                // Failure, notify
                addErrorToView(bindingResult, "Invalid e-mail or password"));
  }

  private boolean sendActivationEmail(final AppUserDto appUserDto, final UUID activationCode) {
    try {
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
    } catch (DailyEmailException e) {
      return false;
    }
    return true;
  }
}
