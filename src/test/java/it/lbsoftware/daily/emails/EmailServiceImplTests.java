package it.lbsoftware.daily.emails;

import static it.lbsoftware.daily.config.Constants.EMAIL_CONTEXT_RESERVED_KEY_MESSAGE;
import static it.lbsoftware.daily.config.Constants.EMAIL_TITLE_KEY;
import static it.lbsoftware.daily.emails.EmailTestUtils.createEmailInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.exceptions.DailyEmailException;
import jakarta.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;

class EmailServiceImplTests extends DailyAbstractUnitTests {

  private static final String TEMPLATE_PATH = "/templates/template.html";
  private static final String TO = "user@email.com";
  private static final String SUBJECT = "daily | notification";
  private static final EmailInfo EMAIL_INFO = createEmailInfo(TEMPLATE_PATH, TO, SUBJECT);
  @Mock private ITemplateEngine templateEngine;
  @Mock private JavaMailSender javaMailSender;
  private EmailServiceImpl emailService;

  private static Stream<Arguments> test1() {
    // EmailInfo, context
    var context = Map.of("key", (Object) "value");
    return Stream.of(arguments(null, null), arguments(null, context), arguments(EMAIL_INFO, null));
  }

  @BeforeEach
  void beforeEach() {
    emailService = new EmailServiceImpl(templateEngine, javaMailSender);
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when send synchronously with null arguments")
  void test1(final EmailInfo emailInfo, final Map<String, Object> context) {
    assertThrows(
        DailyEmailException.class, () -> emailService.sendSynchronously(emailInfo, context));
  }

  @Test
  @ExtendWith(OutputCaptureExtension.class)
  @DisplayName("Should log a warning when send synchronously with reserved key in context")
  void test2(final CapturedOutput capturedOutput) {
    // Given
    var context = Map.of(EMAIL_TITLE_KEY, (Object) "daily notification");
    var mimeMessage = mock(MimeMessage.class);
    given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
    var processedTemplate = "<html>...</html>";
    given(templateEngine.process(eq(EMAIL_INFO.templatePath()), any()))
        .willReturn(processedTemplate);

    // When
    emailService.sendSynchronously(EMAIL_INFO, context);

    // Then
    assertThat(capturedOutput)
        .contains(EMAIL_CONTEXT_RESERVED_KEY_MESSAGE.formatted(EMAIL_TITLE_KEY));
  }

  @Test
  @DisplayName(
      "Should not send synchronously and throw when there are problems configuring the message")
  void test3() {
    // Given
    Map<String, Object> context = Collections.emptyMap();
    var mimeMessage = mock(MimeMessage.class);
    given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
    // null is an invalid text for an e-mail using MimeMessageHelper
    given(templateEngine.process(eq(EMAIL_INFO.templatePath()), any())).willReturn(null);

    // When
    assertThrows(
        DailyEmailException.class, () -> emailService.sendSynchronously(EMAIL_INFO, context));

    // Then
    verify(javaMailSender, times(0)).send(any(MimeMessage.class));
  }

  @Test
  @ExtendWith(OutputCaptureExtension.class)
  @DisplayName("Should throw when send synchronously and there is an e-mail exception")
  void test4(final CapturedOutput capturedOutput) {
    // Given
    var context = Map.of(EMAIL_TITLE_KEY, (Object) "daily notification");
    var mimeMessage = mock(MimeMessage.class);
    given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
    var processedTemplate = "<html>...</html>";
    given(templateEngine.process(eq(EMAIL_INFO.templatePath()), any()))
        .willReturn(processedTemplate);
    doThrow(new MailSendException("Could not send e-mail")).when(javaMailSender).send(mimeMessage);

    // When
    assertThrows(
        DailyEmailException.class, () -> emailService.sendSynchronously(EMAIL_INFO, context));

    // Then
    verify(javaMailSender, times(1)).send(mimeMessage);
  }

  @Test
  @DisplayName("Should send synchronously with valid parameters")
  void test5() {
    // Given
    var context = Map.of(EMAIL_TITLE_KEY, (Object) "daily notification");
    var mimeMessage = mock(MimeMessage.class);
    given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
    var processedTemplate = "<html>...</html>";
    given(templateEngine.process(eq(EMAIL_INFO.templatePath()), any()))
        .willReturn(processedTemplate);

    // When and then
    assertDoesNotThrow(() -> emailService.sendSynchronously(EMAIL_INFO, context));
  }

  @Test
  @DisplayName("Should send asynchronously with valid parameters")
  void test6() {
    // Given
    var context = Map.of(EMAIL_TITLE_KEY, (Object) "daily notification");
    var mimeMessage = mock(MimeMessage.class);
    given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
    var processedTemplate = "<html>...</html>";
    given(templateEngine.process(eq(EMAIL_INFO.templatePath()), any()))
        .willReturn(processedTemplate);

    // When and then
    assertDoesNotThrow(() -> emailService.sendAsynchronously(EMAIL_INFO, context));
  }
}
