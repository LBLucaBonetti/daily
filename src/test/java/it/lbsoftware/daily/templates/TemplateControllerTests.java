package it.lbsoftware.daily.templates;

import static it.lbsoftware.daily.config.Constants.LOGIN_VIEW;
import static it.lbsoftware.daily.config.Constants.REDIRECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class TemplateControllerTests extends DailyAbstractUnitTests {

  private TemplateController templateController;

  @BeforeEach
  void beforeEach() {
    templateController = new TemplateController();
  }

  @Test
  @DisplayName("Should redirect if authenticated when get login")
  void test1() {
    // Given
    Authentication authentication = mock(Authentication.class);

    // When
    var res = templateController.login(authentication);

    // Then
    assertEquals(REDIRECT, res);
  }

  @Test
  @DisplayName("Should return login when get login")
  void test2() {
    // Given
    Authentication authentication = null;

    // When
    var res = templateController.login(authentication);

    // Then
    assertEquals(LOGIN_VIEW, res);
  }
}
