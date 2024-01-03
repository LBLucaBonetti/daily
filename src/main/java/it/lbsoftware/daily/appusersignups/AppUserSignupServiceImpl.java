package it.lbsoftware.daily.appusersignups;

import static it.lbsoftware.daily.appusersettings.AppUserSettingUtils.getAppUserSettings;
import static it.lbsoftware.daily.templates.TemplateUtils.addErrorToView;
import static it.lbsoftware.daily.templates.TemplateUtils.getOauth2AuthProvider;

import it.lbsoftware.daily.appuseractivations.AppUserActivation;
import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusers.AppUserDto;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.emails.EmailInfo;
import it.lbsoftware.daily.emails.EmailService;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserSignupServiceImpl implements AppUserSignupService {

  private final AppUserRepository appUserRepository;
  private final AppUserSettingService appUserSettingService;
  private final AppUserActivationService appUserActivationService;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  @Override
  @Transactional
  public String signup(
      @NonNull AppUserDto appUserDto, @NonNull BindingResult bindingResult, @NonNull Model model) {
    // 1. Show validation errors if any
    if (bindingResult.hasErrors()) {
      return Constants.SIGNUP_VIEW;
    }
    // 2. Passwords should match
    if (!Objects.equals(appUserDto.getPassword(), appUserDto.getPasswordConfirmation())) {
      addErrorToView(bindingResult, "Passwords should match");
      return Constants.SIGNUP_VIEW;
    }
    // 3. Email should not be OAuth2 (users should use the login screen link to perform login with
    // their OAuth2 email address)
    String oauth2Provider = getOauth2AuthProvider(appUserDto.getEmail());
    if (StringUtils.isNotBlank(oauth2Provider)) {
      addErrorToView(
          bindingResult,
          "You are not allowed to sign up with the provided email address. Go back to the login page and use the "
              + oauth2Provider
              + " link to log in");
      return Constants.SIGNUP_VIEW;
    }
    // 3. Email should not be taken
    return appUserRepository
        .findByEmailIgnoreCase(appUserDto.getEmail())
        .map(
            appUser -> {
              addErrorToView(bindingResult, "Invalid email or password");
              return Constants.SIGNUP_VIEW;
            })
        .orElseGet(
            () -> {
              // 4. Sign up the new AppUser
              createDailyAppUser(appUserDto);
              model.addAttribute("signupSuccess", "Check your email to activate your account");
              return Constants.SIGNUP_VIEW;
            });
  }

  /**
   * Creates a new AppUser with the provided information; its auth provider will be DAILY. This
   * method also creates a record for the settings and sends an email to perform the activation; it
   * is not meant to be used without an active HttpServletRequest (i.e.: by an @Async method)
   *
   * @param appUserDto The AppUser data
   */
  private void createDailyAppUser(@NonNull AppUserDto appUserDto) {
    AppUser appUser =
        AppUser.builder()
            .authProvider(AuthProvider.DAILY)
            .enabled(false)
            .firstName(appUserDto.getFirstName())
            .lastName(appUserDto.getLastName())
            .password(passwordEncoder.encode(appUserDto.getPassword()))
            .email(appUserDto.getEmail())
            .build();
    // Save the AppUser
    final UUID appUserUuid = appUserRepository.saveAndFlush(appUser).getUuid();
    // Create settings
    appUserSettingService.createAppUserSettings(getAppUserSettings(appUserDto), appUserUuid);

    // Create the activation link
    final String appUserActivationCode =
        appUserActivationService
            .createAppUserActivation(appUser)
            .map(AppUserActivation::getActivationCode)
            .map(UUID::toString)
            .orElseThrow();
    // Send the activation email
    emailService.send(
        new EmailInfo(
            Constants.EMAIL_APP_USER_ACTIVATION_PATH,
            appUser.getEmail(),
            Constants.EMAIL_APP_USER_ACTIVATION_SUBJECT),
        Map.of(
            "appUserFirstName",
            appUser.getFirstName(),
            "activationUri",
            ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment(Constants.ACTIVATIONS_VIEW, appUserActivationCode)
                .build()
                .toUriString()));
  }
}
