package it.lbsoftware.daily.emails;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final static String EMAIL_FAILURE_MSG = "Failed to send email";

    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void send(@Email @NotNull String to, @NotBlank String subject, @NotBlank String content) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            mimeMessageHelper.setText(content, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("Daily - " + subject);
            mimeMessageHelper.setFrom("noreply@daily.it");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException(EMAIL_FAILURE_MSG);
        }
    }
}