package it.lbsoftware.daily.appusersignups;

import static it.lbsoftware.daily.config.Constants.SIGNUP_SUCCESS;
import static it.lbsoftware.daily.templates.TemplateUtils.DEFAULT_INVALID_CREDENTIALS_ERROR_MESSAGE;
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
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

/** Main {@link it.lbsoftware.daily.appusers.AppUser} signup service implementation. */
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
    // 3. If the e-mail is not taken, creation can be performed
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
                addErrorToView(bindingResult, DEFAULT_INVALID_CREDENTIALS_ERROR_MESSAGE));
  }

  private boolean sendActivationEmail(final AppUserDto appUserDto, final UUID activationCode) {
    try {
      emailService.sendSynchronously(
          new EmailInfo(
              Constants.EMAIL_APP_USER_ACTIVATION_PATH,
              appUserDto.getEmail(),
              Constants.EMAIL_APP_USER_ACTIVATION_SUBJECT),
          Map.of(
              "appUserFirstName",
              Optional.ofNullable(appUserDto.getFirstName())
                  .orElse(Constants.APP_USER_UNSPECIFIED_NAME),
              // We generate the whole URI here instead of building it with the template
              // engine because it will be simpler to change it without touching the html
              // file
              "activationUri",
              appUserActivationService.getActivationUri(activationCode)));
    } catch (DailyEmailException e) {
      log.error("Could not send activation e-mail to AppUser with e-mail " + appUserDto.getEmail());
      return false;
    }
    return true;
  }
}
