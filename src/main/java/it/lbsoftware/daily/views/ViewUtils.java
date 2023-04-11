package it.lbsoftware.daily.views;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public final class ViewUtils {

  private static final String GLOBAL_ERRORS_KEY = "globalError";

  private ViewUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  public static void addErrorToView(
      @NonNull final BindingResult bindingResult, @NonNull final String errorMessage) {
    bindingResult.addError(new ObjectError(GLOBAL_ERRORS_KEY, errorMessage));
  }

  public static String getOauth2AuthProvider(@NonNull final String email) {
    return email.endsWith("@gmail.com") ? "Google" : StringUtils.EMPTY;
  }
}
