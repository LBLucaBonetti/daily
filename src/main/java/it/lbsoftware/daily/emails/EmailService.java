package it.lbsoftware.daily.emails;

import org.springframework.scheduling.annotation.Async;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface EmailService {

    /**
     * Sends an email
     *
     * @param to      Valid email address of the single recipient
     * @param subject The subject for the email, automatically prepended with the app name
     * @param content The content for the email
     */
    @Async
    void send(@Email @NotNull String to, @NotBlank String subject, @NotBlank String content);

}
