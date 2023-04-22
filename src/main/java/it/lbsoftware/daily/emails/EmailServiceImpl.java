package it.lbsoftware.daily.emails;

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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@CommonsLog
public class EmailServiceImpl implements EmailService {

  private static final String FROM = "daily@trydaily.click";
  private static final String TITLE_KEY = "title";
  private final TemplateEngine templateEngine;
  private final JavaMailSender javaMailSender;

  @Override
  @Async
  public void send(@NonNull EmailInfo emailInfo, @NonNull Map<String, Object> context) {
    Context templateContext = new Context();
    if (context.containsKey(TITLE_KEY)) {
      log.warn("The context key " + TITLE_KEY + " is reserved and will be ignored");
    }
    templateContext.setVariables(context);
    templateContext.setVariable(TITLE_KEY, emailInfo.subject());

    String processedTemplate = templateEngine.process(emailInfo.templatePath(), templateContext);
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

    try {
      mimeMessageHelper.setSubject(emailInfo.subject());
      mimeMessageHelper.setTo(emailInfo.to());
      mimeMessageHelper.setFrom(FROM);
      mimeMessageHelper.setText(processedTemplate, true);
    } catch (MessagingException messagingException) {
      log.error(
          "Problems configuring email with the template "
              + emailInfo.templatePath()
              + "; no email will be sent",
          messagingException);
      return;
    }

    try {
      javaMailSender.send(mimeMessage);
      log.info(
          "Successfully sent email with the template "
              + emailInfo.templatePath()
              + " to "
              + emailInfo.to());
    } catch (MailException mailException) {
      log.error(
          "Could not send email with the template " + emailInfo.templatePath(), mailException);
    }
  }
}
