package it.lbsoftware.daily.templates;

import static it.lbsoftware.daily.config.Constants.REDIRECT;

import java.util.Optional;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public final class TemplateUtils {

  private static final String GLOBAL_ERRORS_KEY = "globalError";

  private TemplateUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  public static void addErrorToView(
      @NonNull final BindingResult bindingResult, @NonNull final String errorMessage) {
    bindingResult.addError(new ObjectError(GLOBAL_ERRORS_KEY, errorMessage));
  }

  public static Optional<String> redirectIfAuthenticated(final Authentication authentication) {
    return Optional.ofNullable(authentication).map(auth -> REDIRECT);
  }
}
