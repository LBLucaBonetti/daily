package it.lbsoftware.daily.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

class DailyReferrerPolicyHeaderWriterTests extends DailyAbstractUnitTests {

  @Test
  @DisplayName("Should throw when referrer policy value is null")
  void test1() {
    // Given
    String referrerPolicyValue = null;

    // When
    var res =
        assertThrows(
            IllegalArgumentException.class,
            () -> new DailyReferrerPolicyHeaderWriter(referrerPolicyValue));

    // Then
    assertNotNull(res);
  }

  @Test
  @DisplayName("Should set header value")
  void test2() {
    // Given
    var referrerPolicyValue = ReferrerPolicy.NO_REFERRER.getPolicy();
    var httpServletRequest = new MockHttpServletRequest();

    // When
    var res = new MockHttpServletResponse();
    new DailyReferrerPolicyHeaderWriter(referrerPolicyValue).writeHeaders(httpServletRequest, res);

    // Then
    assertEquals(
        res.getHeader(DailyReferrerPolicyHeaderWriter.REFERRER_POLICY_HEADER), referrerPolicyValue);
  }
}
