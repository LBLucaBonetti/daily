package it.lbsoftware.daily.frontend;

import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/**
 * Generic operation result. It accepts a binary result, either ok or error, and a message that is
 * optional when the operation is ok and is mandatory when it is an error. Note that while the
 * message may be mandatory, the values can be null but the results can still be used with the
 * {@link OperationResult#ifHasMessageDo(BiConsumer)} method.
 */
public class OperationResult {

  private final String messageKey;
  private final String messageValue;

  /** When true, indicates the operation has been correctly completed. */
  private final boolean ok;

  private OperationResult(final boolean ok, final String messageKey, final String messageValue) {
    this.ok = ok;
    this.messageKey = messageKey;
    this.messageValue = messageValue;
  }

  /**
   * Creates a positive operation result with no message.
   *
   * @return A positive operation result
   */
  public static OperationResult ok() {
    return new OperationResult(true, null, null);
  }

  /**
   * Creates a positive operation result with a message.
   *
   * @param messageKey The message key
   * @param messageValue The message value
   * @return A positive operation result
   */
  public static OperationResult ok(final String messageKey, final String messageValue) {
    return new OperationResult(true, messageKey, messageValue);
  }

  /**
   * Creates a negative operation result with a message.
   *
   * @param messageKey The message key
   * @param messageValue The message value
   * @return A negative operation result
   */
  public static OperationResult error(final String messageKey, final String messageValue) {
    return new OperationResult(false, messageKey, messageValue);
  }

  /**
   * Determines whether the operation result is negative.
   *
   * @return True if the operation result is negative (error), false otherwise
   */
  public boolean isError() {
    return !isOk();
  }

  /**
   * Determines whether the operation result is positive.
   *
   * @return True if the operation result is positive (ok), false otherwise
   */
  public boolean isOk() {
    return ok;
  }

  /**
   * Performs the provided operation only if the operation result has a valid non-blank message.
   *
   * @param messageConsumer The operation to perform with the message key and value
   */
  public void ifHasMessageDo(final BiConsumer<String, String> messageConsumer) {
    if (hasNoMessage()) {
      return;
    }
    messageConsumer.accept(messageKey, messageValue);
  }

  /**
   * Checks if the operation result has an attached message.
   *
   * @return True if the operation result has an attached message, false otherwise
   */
  public boolean hasMessage() {
    return !hasNoMessage();
  }

  private boolean hasNoMessage() {
    return Stream.of(messageKey, messageValue).anyMatch(StringUtils::isBlank);
  }
}
