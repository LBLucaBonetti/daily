package it.lbsoftware.daily.appusers;

import static it.lbsoftware.daily.views.ViewUtils.addErrorToView;
import static it.lbsoftware.daily.views.ViewUtils.getOauth2AuthProvider;

import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import it.lbsoftware.daily.appusersettings.AppUserSettingDto;
import it.lbsoftware.daily.appusersettings.AppUserSettingService;
import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.config.Constants;
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
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
@CommonsLog
public class AppUserServiceImpl implements AppUserService {

  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final AppUserSettingService appUserSettingService;

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
  public String signup(@NonNull AppUserDto appUserDto, @NonNull BindingResult bindingResult) {
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
              addErrorToView(bindingResult, "Invalid email and/or password");
              return Constants.SIGNUP_VIEW;
            })
        .orElseGet(
            () -> {
              // 4. Sign up the new AppUser
              createDailyAppUser(appUserDto);
              return Constants.LOGIN_VIEW;
            });
  }

  @Override
  @Transactional
  public void createDailyAppUser(@NonNull AppUserDto appUserDto) {
    AppUser appUser =
        AppUser.builder()
            .authProvider(AuthProvider.DAILY)
            .enabled(true)
            .firstName(appUserDto.getFirstName())
            .lastName(appUserDto.getLastName())
            .password(passwordEncoder.encode(appUserDto.getPassword()))
            .email(appUserDto.getEmail())
            .build();
    final UUID appUserUuid = appUserRepository.save(appUser).getUuid();
    appUserSettingService.createAppUserSettings(getAppUserSettings(appUserDto), appUserUuid);
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
    final UUID appUserUuid = appUserRepository.save(appUser).getUuid();
    appUserSettingService.createAppUserSettings(getAppUserSettings(appUserDto), appUserUuid);
  }

  private AppUserSettingDto getAppUserSettings(@NonNull AppUserDto appUser) {
    AppUserSettingDto appUserSetting = new AppUserSettingDto();
    appUserSetting.setLang(appUser.getLang());
    return appUserSetting;
  }
}
