package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.templates.TemplateUtils.addErrorToView;
import static it.lbsoftware.daily.templates.TemplateUtils.getOauth2AuthProvider;

import it.lbsoftware.daily.appuseractivations.AppUserActivation;
import it.lbsoftware.daily.appuseractivations.AppUserActivationService;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusersettings.AppUserSettingDto;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.emails.EmailInfo;
import it.lbsoftware.daily.emails.EmailService;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserServiceImpl implements AppUserService {

  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final AppUserSettingService appUserSettingService;
  private final EmailService emailService;
  private final AppUserActivationService appUserActivationService;

  @Override
  public UUID getUuid(@NonNull Object principal) {
    return Optional.ofNullable(getAppUserInfo(principal).email())
        .flatMap(appUserRepository::findByEmailIgnoreCase)
        .map(BaseEntity::getUuid)
        .orElseThrow();
  }

  @Override
  public InfoDto getAppUserInfo(@NonNull Object principal) {
    String fullName;
    String email;
    if (principal instanceof OidcUser appUserOidcUser) {
      fullName = appUserOidcUser.getFullName();
      email = appUserOidcUser.getEmail();
    } else if (principal instanceof AppUserDetails appUserDetails) {
      fullName = appUserDetails.getFullname();
      email = appUserDetails.getUsername();
    } else {
      log.warn("Invalid AppUser instance detected");
      throw new IllegalStateException();
    }
    return new InfoDto(fullName, email);
  }

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
              model.addAttribute("signupSuccess","Check your email to activate your account");
              return Constants.SIGNUP_VIEW;
            });
  }

  @Override
  @Transactional
  public boolean activate(UUID activationCode) {
    return appUserActivationService
        .readAppUserActivation(activationCode)
        .filter(
            appUserActivation ->
                !appUserActivationService.isActivated(appUserActivation)
                    && appUserActivationService.isValid(appUserActivation))
        .map(
            appUserActivation -> {
              appUserActivationService.setActivated(appUserActivation);
              AppUser appUser = appUserActivation.getAppUser();
              appUser.setEnabled(true);
              appUserRepository.save(appUser);
              return true;
            })
        .orElse(false);
  }

  @Override
  @Transactional
  public void createDailyAppUser(@NonNull AppUserDto appUserDto) {
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

  @Override
  @Transactional
  public void createOauth2AppUser(
      @NonNull AppUserDto appUserDto,
      @NonNull AuthProvider authProvider,
      @NonNull String authProviderId) {
    if (AuthProvider.DAILY.equals(authProvider)) {
      throw new IllegalArgumentException();
    }
    AppUser appUser =
        AppUser.builder()
            .authProvider(authProvider)
            .authProviderId(authProviderId)
            .enabled(true)
            .email(appUserDto.getEmail())
            .build();
    final UUID appUserUuid = appUserRepository.saveAndFlush(appUser).getUuid();
    appUserSettingService.createAppUserSettings(getAppUserSettings(appUserDto), appUserUuid);
  }

  private AppUserSettingDto getAppUserSettings(AppUserDto appUser) {
    AppUserSettingDto appUserSetting = new AppUserSettingDto();
    appUserSetting.setLang(appUser.getLang());
    return appUserSetting;
  }
}
