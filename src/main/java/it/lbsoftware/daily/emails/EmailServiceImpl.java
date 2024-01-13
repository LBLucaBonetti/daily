package it.lbsoftware.daily.emails;

import static it.lbsoftware.daily.config.Constants.EMAIL_CONTEXT_RESERVED_KEY_MESSAGE;
import static it.lbsoftware.daily.config.Constants.EMAIL_FROM;
import static it.lbsoftware.daily.config.Constants.EMAIL_SEND_ERROR_MESSAGE;
import static it.lbsoftware.daily.config.Constants.EMAIL_TITLE_KEY;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@CommonsLog
public class EmailServiceImpl implements EmailService {

  private final ITemplateEngine templateEngine;
  private final JavaMailSender javaMailSender;

  @Override
  @Async
  public void send(@NonNull EmailInfo emailInfo, @NonNull Map<String, Object> context) {
    Context templateContext = new Context();
    if (context.containsKey(EMAIL_TITLE_KEY)) {
      log.warn(EMAIL_CONTEXT_RESERVED_KEY_MESSAGE.formatted(EMAIL_TITLE_KEY));
    }
    templateContext.setVariables(context);
    templateContext.setVariable(EMAIL_TITLE_KEY, emailInfo.subject());

    String processedTemplate = templateEngine.process(emailInfo.templatePath(), templateContext);
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

    try {
      mimeMessageHelper.setSubject(emailInfo.subject());
      mimeMessageHelper.setTo(emailInfo.to());
      mimeMessageHelper.setFrom(EMAIL_FROM);
      mimeMessageHelper.setText(processedTemplate, true);
    } catch (IllegalArgumentException | MessagingException configurationException) {
      log.error(
          "Problems configuring e-mail with the template "
              + emailInfo.templatePath()
              + "; no e-mail will be sent",
          configurationException);
      return;
    }

    try {
      javaMailSender.send(mimeMessage);
      log.info(
          "Successfully sent e-mail with the template "
              + emailInfo.templatePath()
              + " to "
              + emailInfo.to());
    } catch (MailException mailException) {
      log.error(EMAIL_SEND_ERROR_MESSAGE.formatted(emailInfo.templatePath()), mailException);
    }
  }
}
