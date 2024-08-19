package it.lbsoftware.daily.money;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static it.lbsoftware.daily.money.MoneyTestUtils.createMoneyDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
import it.lbsoftware.daily.money.Money.OperationType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class MoneyControllerTests extends DailyAbstractUnitTests {

  private static final LocalDate OPERATION_DATE = LocalDate.now();
  private static final BigDecimal AMOUNT = BigDecimal.TEN;
  private static final OperationType OPERATION_TYPE = OperationType.INCOME;
  private static final String DESCRIPTION = "description";
  private static final String EMAIL = "appuser@email.com";
  private static final UUID UNIQUE_ID = UUID.randomUUID();
  private static final AppUser APP_USER = createAppUser(UNIQUE_ID, EMAIL);
  @Mock private MoneyService moneyService;
  @Mock private AppUserService appUserService;
  @Mock private OidcUser appUser;
  @Mock private Pageable pageable;
  private MoneyController moneyController;

  @BeforeEach
  void beforeEach() {
    moneyController = new MoneyController(moneyService, appUserService);
    given(appUserService.getAppUser(appUser)).willReturn(APP_USER);
  }

  @Test
  @DisplayName("Should read money and return ok")
  void test1() {
    // Given
    var moneyDto =
        createMoneyDto(UUID.randomUUID(), OPERATION_DATE, AMOUNT, OPERATION_TYPE, DESCRIPTION);
    Page<MoneyDto> readMoney = new PageImpl<>(List.of(moneyDto));
    var from = LocalDate.now().minusDays(1);
    given(moneyService.readMoney(pageable, from, APP_USER)).willReturn(readMoney);

    // When
    ResponseEntity<PageDto<MoneyDto>> res = moneyController.readMoney(pageable, from, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(moneyService, times(1)).readMoney(pageable, from, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(res.getBody());
    assertNotNull(res.getBody().getContent());
    assertEquals(readMoney.getContent().size(), res.getBody().getContent().size());
    assertEquals(moneyDto, res.getBody().getContent().getFirst());
  }

  @Test
  @DisplayName(
      "Should not read money because of wrong money field name as sort parameter and return bad request")
  void test2() {
    // Given
    var from = LocalDate.now().minusDays(1);
    doThrow(new RuntimeException("Wrong field name as sort parameter"))
        .when(moneyService)
        .readMoney(pageable, from, APP_USER);

    // When
    DailyBadRequestException res =
        assertThrows(
            DailyBadRequestException.class,
            () -> moneyController.readMoney(pageable, from, appUser));

    // Then
    assertNotNull(res);
    verify(moneyService, times(1)).readMoney(pageable, from, APP_USER);
    assertNull(res.getMessage());
  }
}
