package it.lbsoftware.daily.emails;

import java.util.Map;

/** Service to deal with e-mails, either synchronously or asynchronously. */
public interface EmailService {

  /**
   * Sends an e-mail synchronously.
   *
   * @param emailInfo E-mail information
   * @param context E-mail context, used to build the template
   */
  void sendSynchronously(EmailInfo emailInfo, Map<String, Object> context);

  /**
   * Sends an e-mail asynchronously; note that the method immediately returns, so do not try to
   * catch exceptions because they will not be propagated to the caller; this method should be used
   * when the result of sending an e-mail is not crucial for the current business operations, or a
   * specific way to retrieve the exceptions should be used.
   *
   * @param emailInfo E-mail information
   * @param context E-mail context, used to build the template
   * @implNote The implementation of this method should be asynchronous either from a
   *     framework-specific mechanism or from its own code (such as forking when called)
   */
  void sendAsynchronously(EmailInfo emailInfo, Map<String, Object> context);
}
