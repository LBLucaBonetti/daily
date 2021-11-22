package it.lbsoftware.daily.emails;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final String EMAIL_FAILURE_MSG = "Failed to send email";

    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void send(String to, String subject, String content) {
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