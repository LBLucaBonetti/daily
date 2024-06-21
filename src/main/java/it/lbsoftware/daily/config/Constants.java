package it.lbsoftware.daily.config;

import java.util.List;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

/** Application-specific constants. */
public final class Constants {

  public static final String REDIRECT = "redirect:/";
  public static final String COOKIE_CSRF_TOKEN_REPOSITORY_BEAN_NAME = "cookieCsrfTokenRepository";
  // Allow everything but only from the same origin & Bootstrap CDN & Bunny Fonts CDN
  public static final String CONTENT_SECURITY_POLICY =
      "default-src 'self' https://cdn.jsdelivr.net/npm/ https://fonts.bunny.net/";
  public static final ReferrerPolicy REFERRER_POLICY =
      ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN;
  public static final String PERMISSIONS_POLICY = "camera=(), microphone=(), geolocation=()";
  public static final String LOCALDATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String DAILY_COOKIE_CSRF_ENHANCED_SECURITY_KEY =
      "daily.cookie.csrf.enhanced.security";
  public static final String DAILY_ASYNC_ENABLED = "daily.async.enabled";
  public static final String DAILY_SCHEDULING_ENABLED = "daily.scheduling.enabled";
  public static final String ERROR_KEY = "error";
  public static final String ERROR_PREFIX = "daily.error.";
  public static final String ERROR_DEFAULT = ERROR_PREFIX + "default";
  public static final String ERROR_NOTE_TAGS_MAX = ERROR_PREFIX + "note.tags.max";
  public static final String ERROR_NOT_FOUND = ERROR_PREFIX + "not.found";
  public static final String DO_NOT_STORE_NULL_SPEL = "#result == null";
  public static final String BASIC_SINGLE_ENTITY_CACHE_KEY_SPEL =
      "'appUser:' + #appUser + ':' + #uuid";
  public static final String NOTE_TAGS_CACHE_KEY_SPEL =
      BASIC_SINGLE_ENTITY_CACHE_KEY_SPEL + " + ':tags'";
  public static final String TAG_CACHE = "tag";
  public static final String NOTE_CACHE = "note";
  public static final int NOTE_TEXT_MAX = 255;
  public static final int TAG_NAME_MAX = 30;
  public static final int NOTE_TAGS_MAX = 5;
  public static final String TAG_COLOR_HEX_REGEXP = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";
  public static final int APP_USER_EMAIL_MAX = 320;
  public static final int APP_USER_FIRST_NAME_MAX = 255;
  public static final int APP_USER_LAST_NAME_MAX = 255;
  public static final int APP_USER_AUTH_PROVIDER_MAX = 255;
  public static final String APP_USER_LANG_REGEXP = "^(en-US|it)$";
  public static final String APP_USER_LANG_PATTERN_MESSAGE = "Invalid language";
  public static final String APP_USER_UNSPECIFIED_NAME = "app user";
  public static final String LOGIN_VIEW = "login";
  public static final String LOGIN_PATH = "/" + LOGIN_VIEW;
  public static final String SIGNUP_VIEW = "signup";
  public static final String SIGNUP_PATH = "/" + SIGNUP_VIEW;
  public static final String ERROR_VIEW = "error";
  public static final String ERROR_PATH = "/" + ERROR_VIEW;
  public static final String INVALID_CREDENTIALS_ERROR = "invalidCredentialsError";
  public static final String ACTIVATIONS_VIEW = "activations";
  public static final String ACTIVATION_CODE = "activation-code";
  public static final String ACTIVATION_PATH =
      "/" + ACTIVATIONS_VIEW + "/" + "{" + ACTIVATION_CODE + "}";
  public static final String SEND_PASSWORD_RESET_NOTIFICATION_VIEW = "password-reset-notification";
  public static final String SEND_PASSWORD_RESET_NOTIFICATION_PATH =
      "/" + SEND_PASSWORD_RESET_NOTIFICATION_VIEW;
  public static final String PASSWORD_RESET_VIEW = "password-reset";
  public static final String PASSWORD_RESET_CODE = "code";
  public static final String PASSWORD_RESET_PATH = "/" + PASSWORD_RESET_VIEW;
  public static final List<String> ALLOWED_STATIC_TEMPLATES =
      List.of(
          SIGNUP_PATH,
          ERROR_PATH,
          ACTIVATION_PATH,
          SEND_PASSWORD_RESET_NOTIFICATION_PATH,
          PASSWORD_RESET_PATH);
  public static final String ACTIVATION_CODE_SUCCESS = "activationCodeSuccess";
  public static final String ACTIVATION_CODE_FAILURE = "activationCodeFailure";
  public static final String SIGNUP_SUCCESS = "signupSuccess";
  public static final String PASSWORD_RESET_NOTIFICATION_SUCCESS =
      "passwordResetNotificationSuccess";
  public static final String PASSWORD_RESET_NOTIFICATION_SUCCESS_MESSAGE =
      "If you signed up with the provided e-mail, you will receive a notification to reset your "
          + "password";
  public static final List<String> ALLOWED_STATIC_ASSETS =
      List.of(
          "/public-style.css",
          "/img/daily-logo.svg",
          "/favicon.ico",
          "/img/google-logo.svg",
          "/img/github-logo.svg",
          "/public-login.js",
          "/public-signup.js",
          "/img/logo.svg");
  public static final String NOT_BLANK_MESSAGE = "Please fill out this field";
  public static final String EMAIL_SUBJECT_PREFIX = "daily | ";
  public static final String EMAIL_PATH_PREFIX = "emails/";
  public static final String EMAIL_APP_USER_ACTIVATION_PATH =
      EMAIL_PATH_PREFIX + "app_user_activation.html";
  public static final String EMAIL_APP_USER_ACTIVATION_SUBJECT =
      EMAIL_SUBJECT_PREFIX + "Activate your account";
  public static final String EMAIL_APP_USER_REMOVAL_NOTIFICATION_PATH =
      EMAIL_PATH_PREFIX + "app_user_removal_notification.html";
  public static final String EMAIL_APP_USER_REMOVAL_NOTIFICATION_SUBJECT =
      EMAIL_SUBJECT_PREFIX + "Action required";
  public static final String EMAIL_APP_USER_PASSWORD_RESET_PATH =
      EMAIL_PATH_PREFIX + "app_user_password_reset.html";
  public static final String EMAIL_APP_USER_PASSWORD_RESET_SUBJECT =
      EMAIL_SUBJECT_PREFIX + "Reset password";
  public static final String EMAIL_FROM = "daily@trydaily.click";
  public static final String EMAIL_TITLE_KEY = "title";
  public static final String EMAIL_CONTEXT_RESERVED_KEY_MESSAGE =
      "The context key %s is reserved and will be ignored";
  public static final int REMOVAL_NOTIFICATION_THRESHOLD_DAYS = 30;
  public static final int REMOVAL_NOTIFICATION_TO_REMOVAL_DELTA_DAYS = 2;
  public static final int REMOVAL_THRESHOLD_DAYS =
      REMOVAL_NOTIFICATION_THRESHOLD_DAYS + REMOVAL_NOTIFICATION_TO_REMOVAL_DELTA_DAYS;
  public static final int FAILURES_THRESHOLD = 3;
  public static final int PASSWORD_RESET_NOTIFICATION_THRESHOLD_MINUTES = 30;

  private Constants() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }
}
