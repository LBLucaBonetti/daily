package it.lbsoftware.daily.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.web.header.HeaderWriter;

/** Writes the referrer policy header, overwriting any pre-existing value. */
public class DailyReferrerPolicyHeaderWriter implements HeaderWriter {

  public static final String REFERRER_POLICY_HEADER = "Referrer-Policy";
  private final String referrerPolicyValue;

  /**
   * Creates a new header writer with the given referrer policy value to set.
   *
   * @param referrerPolicyValue The referrer policy value to set
   */
  public DailyReferrerPolicyHeaderWriter(@NonNull final String referrerPolicyValue) {
    this.referrerPolicyValue = referrerPolicyValue;
  }

  @Override
  public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
    response.setHeader(REFERRER_POLICY_HEADER, referrerPolicyValue);
  }
}
