package it.lbsoftware.daily.emails;

/**
 * Contains information about an e-mail.
 *
 * @param templatePath The template path, relative to the configured (or default) template resources
 *     base path
 * @param to The e-mail address of the receiver to send the e-mail to
 * @param subject The subject of the e-mail
 */
public record EmailInfo(String templatePath, String to, String subject) {}
