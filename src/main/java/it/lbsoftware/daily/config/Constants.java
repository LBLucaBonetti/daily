package it.lbsoftware.daily.config;

public final class Constants {

  public static final String COOKIE_CSRF_TOKEN_REPOSITORY_BEAN_NAME = "cookieCsrfTokenRepository";
  public static final String CSRF_TOKEN_NAME = "XSRF-TOKEN";
  public static final String CONTENT_SECURITY_POLICY = "default-src 'self'";
  public static final String PERMISSIONS_POLICY = "camera=(), microphone=(), geolocation=()";
  public static final String DAILY_COOKIE_CSRF_SECURE_KEY = "daily.cookie.csrf.secure";
  public static final String DAILY_COOKIE_CSRF_SAME_SITE_KEY = "daily.cookie.csrf.same-site";
  public static final String ERROR_KEY = "error";
  public static final String ERROR_DEFAULT = "error.default";
  public static final String BASIC_SINGLE_ENTITY_CACHE_KEY_SPEL =
      "'appUser:' + #appUser + ':' + #uuid";
  public static final String TAG_CACHE = "tag";
  public static final String NOTE_CACHE = "note";
  public static final int NOTE_TEXT_MAX = 255;
  public static final int TAG_NAME_MAX = 31;
  public static final int NOTE_TAGS_MAX = 5;
  public static final String ERROR_NOTE_TAGS_MAX = "error.note.tags.max";

  private Constants() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }
}
