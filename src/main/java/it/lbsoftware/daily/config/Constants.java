package it.lbsoftware.daily.config;

public final class Constants {

  public static final String ERROR_KEY = "error";
  public static final String ERROR_DEFAULT = "error.default";
  public static final int NOTE_TEXT_MAX = 255;
  public static final int TAG_NAME_MAX = 31;
  public static final int NOTE_TAGS_MAX = 5;
  public static final String ERROR_NOTE_TAGS_MAX = "error.note.tags.max";

  private Constants() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }
}
