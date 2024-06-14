package it.lbsoftware.daily.templates;

import static it.lbsoftware.daily.config.Constants.REDIRECT;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/** Utilities to deal with templates. */
public final class TemplateUtils {

  public static final String DEFAULT_INVALID_CREDENTIALS_ERROR_MESSAGE =
      "Invalid e-mail and/or password";
  public static final String DEFAULT_COMPROMISED_PASSWORD_ERROR_MESSAGE =
      "The provided password is compromised and should not be used. Please change it";
  private static final String GLOBAL_ERRORS_KEY = "globalError";

  private TemplateUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * Add the provided error message to the provided binding result, using a default {@link
   * TemplateUtils#GLOBAL_ERRORS_KEY} key.
   *
   * @param bindingResult The binding result to add the error message to
   * @param errorMessage The error message to add
   */
  public static void addErrorToView(
      @NonNull final BindingResult bindingResult, @NonNull final String errorMessage) {
    bindingResult.addError(new ObjectError(GLOBAL_ERRORS_KEY, errorMessage));
  }

  /**
   * Redirects if the authentication is missing.
   *
   * @param authentication The subject authentication
   * @return A redirect if the authentication is present or an empty value
   */
  public static Optional<String> redirectIfAuthenticated(final Authentication authentication) {
    return Optional.ofNullable(authentication).map(auth -> REDIRECT);
  }

  /**
   * Determines whether the request has a non-null parameter with the provided name.
   *
   * @param request The subject request
   * @param parameterName The subject parameter name to check
   * @return True, if the request is not null, the parameter name is not null and the request has a
   *     non-null value for the provided parameter name, false otherwise
   */
  public static boolean hasNonNullParameter(
      final ServletRequest request, final String parameterName) {
    return request != null && parameterName != null && request.getParameter(parameterName) != null;
  }

  /**
   * Retrieves the invalid-credentials error message from the request, if any. The error message
   * either is from a mapped specific exception or from a default one.
   *
   * @param request The incoming request
   * @return An appropriate invalid-credentials error message, ready to be shown to the end user
   */
  public static String getInvalidCredentialsErrorMessage(final HttpServletRequest request) {
    var session = request.getSession(false);
    if (session == null) {
      return DEFAULT_INVALID_CREDENTIALS_ERROR_MESSAGE;
    }
    if (!(session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)
        instanceof CompromisedPasswordException)) {
      return DEFAULT_INVALID_CREDENTIALS_ERROR_MESSAGE;
    }
    // Here we are currently dealing with a CompromisedPasswordException
    return DEFAULT_COMPROMISED_PASSWORD_ERROR_MESSAGE;
  }
}
