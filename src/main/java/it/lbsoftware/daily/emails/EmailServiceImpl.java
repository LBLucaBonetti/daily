package it.lbsoftware.daily.emails;

import static it.lbsoftware.daily.config.Constants.EMAIL_CONTEXT_RESERVED_KEY_MESSAGE;
import static it.lbsoftware.daily.config.Constants.EMAIL_FROM;
import static it.lbsoftware.daily.config.Constants.EMAIL_TITLE_KEY;

import it.lbsoftware.daily.exceptions.DailyEmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

/** Main e-mail service implementation. */
@Service
@RequiredArgsConstructor
@CommonsLog
public class EmailServiceImpl implements EmailService {

  private final ITemplateEngine templateEngine;
  private final JavaMailSender javaMailSender;

  @Override
  public void sendSynchronously(final EmailInfo emailInfo, final Map<String, Object> context) {
    send(emailInfo, context);
  }

  @Override
  @Async
  public void sendAsynchronously(final EmailInfo emailInfo, final Map<String, Object> context) {
    send(emailInfo, context);
  }

  private void send(final EmailInfo emailInfo, final Map<String, Object> context) {
    try {
      sendEmail(emailInfo, context);
    } catch (Exception e) {
      log.error("Problems sending the e-mail", e);
      throw new DailyEmailException();
    }
  }

  private void sendEmail(
      @NonNull final EmailInfo emailInfo, @NonNull final Map<String, Object> context)
      throws MessagingException {
    Context templateContext = prepareTemplateContext(emailInfo, context);
    String processedTemplate = templateEngine.process(emailInfo.templatePath(), templateContext);
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    prepareMimeMessageWithHelper(emailInfo, mimeMessage, processedTemplate);
    javaMailSender.send(mimeMessage);
    log.info(
        "Successfully sent e-mail with the template "
            + emailInfo.templatePath()
            + " to "
            + emailInfo.to());
  }

  private Context prepareTemplateContext(
      final EmailInfo emailInfo, final Map<String, Object> context) {
    Context templateContext = new Context();
    if (context.containsKey(EMAIL_TITLE_KEY)) {
      log.warn(EMAIL_CONTEXT_RESERVED_KEY_MESSAGE.formatted(EMAIL_TITLE_KEY));
    }
    templateContext.setVariables(context);
    templateContext.setVariable(EMAIL_TITLE_KEY, emailInfo.subject());
    return templateContext;
  }

  private void prepareMimeMessageWithHelper(
      final EmailInfo emailInfo, final MimeMessage mimeMessage, final String processedTemplate)
      throws MessagingException {
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
    mimeMessageHelper.setSubject(emailInfo.subject());
    mimeMessageHelper.setTo(emailInfo.to());
    mimeMessageHelper.setFrom(EMAIL_FROM);
    mimeMessageHelper.setText(processedTemplate, true);
  }
}
