package it.lbsoftware.daily.emails;

public final class EmailTestUtils {

  private EmailTestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * {@link EmailInfo} generator.
   *
   * @param templatePath The template path
   * @param to The receiver of this e-mail
   * @param subject The subject for this e-mail
   * @return The created {@link EmailInfo}
   */
  public static EmailInfo createEmailInfo(
      final String templatePath, final String to, final String subject) {
    return new EmailInfo(templatePath, to, subject);
  }
}
