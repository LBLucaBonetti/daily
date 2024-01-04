package it.lbsoftware.daily.appuseractivations;

import static it.lbsoftware.daily.config.Constants.ACTIVATIONS_VIEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class AppUserActivationServiceImplTests extends DailyAbstractUnitTests {

  @Mock private AppUserActivationRepository appUserActivationRepository;
  private AppUserActivationServiceImpl appUserActivationService;

  @BeforeEach
  void beforeEach() {
    appUserActivationService = new AppUserActivationServiceImpl(appUserActivationRepository);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "abc"})
  @DisplayName("Should throw when provide activation uri with invalid activation code")
  void test1(final String activationCode) {
    assertThrows(
        IllegalArgumentException.class,
        () -> appUserActivationService.getActivationUri(activationCode));
  }

  @Test
  @DisplayName("Should provide activation uri")
  void test2() {
    // Given
    var mockHttpServletRequest = new MockHttpServletRequest();
    var prefix = mockHttpServletRequest.getRequestURL();
    var baseUri = "/daily";
    var activationCode = UUID.randomUUID().toString();
    mockHttpServletRequest.setContextPath(baseUri);
    var servletRequestAttributes = new ServletRequestAttributes(mockHttpServletRequest);
    RequestContextHolder.setRequestAttributes(servletRequestAttributes);

    // When
    var res = appUserActivationService.getActivationUri(activationCode);

    // Then
    assertEquals(prefix + baseUri + "/" + ACTIVATIONS_VIEW + "/" + activationCode, res);
  }
}
