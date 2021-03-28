package it.lbsoftware.daily.emails;

import org.springframework.scheduling.annotation.Async;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public interface EmailService {

    @Async
    void send(@Email @NotNull String to, @NotBlank String subject, @NotBlank String content);

}
