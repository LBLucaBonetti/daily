package it.lbsoftware.daily.config;

public final class Constants {

  public static final String COOKIE_CSRF_TOKEN_REPOSITORY_BEAN_NAME = "cookieCsrfTokenRepository";
  public static final String CSRF_TOKEN_NAME = "XSRF-TOKEN";
  public static final String CONTENT_SECURITY_POLICY = "default-src 'self'";
  public static final String PERMISSIONS_POLICY = "camera=(), microphone=(), geolocation=()";
  public static final String LOCALDATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String DAILY_COOKIE_CSRF_SECURE_KEY = "daily.cookie.csrf.secure";
  public static final String DAILY_COOKIE_CSRF_SAME_SITE_KEY = "daily.cookie.csrf.same-site";
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
  public static final String LOGIN_VIEW = "login";
  public static final String LOGIN_PATH = "/" + LOGIN_VIEW;
  public static final String SIGNUP_VIEW = "signup";
  public static final String SIGNUP_PATH = "/" + SIGNUP_VIEW;

  private Constants() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }
}
