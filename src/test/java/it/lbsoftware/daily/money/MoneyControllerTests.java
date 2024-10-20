package it.lbsoftware.daily.money;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static it.lbsoftware.daily.money.MoneyTestUtils.createMoneyDto;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
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
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
import it.lbsoftware.daily.exceptions.DailyConflictException;
import it.lbsoftware.daily.exceptions.DailyNotFoundException;
import it.lbsoftware.daily.money.Money.OperationType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    var yearMonthDate = LocalDate.now().minusDays(1);
    var from = yearMonthDate.with(firstDayOfMonth());
    var to = yearMonthDate.with(lastDayOfMonth());
    given(moneyService.readMoney(pageable, from, to, APP_USER)).willReturn(readMoney);

    // When
    ResponseEntity<PageDto<MoneyDto>> res =
        moneyController.readMoney(pageable, yearMonthDate, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(moneyService, times(1)).readMoney(pageable, from, to, APP_USER);
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
    var yearMonthDate = LocalDate.now().minusDays(1);
    var from = yearMonthDate.with(firstDayOfMonth());
    var to = yearMonthDate.with(lastDayOfMonth());
    doThrow(new RuntimeException("Wrong field name as sort parameter"))
        .when(moneyService)
        .readMoney(pageable, from, to, APP_USER);

    // When
    DailyBadRequestException res =
        assertThrows(
            DailyBadRequestException.class,
            () -> moneyController.readMoney(pageable, yearMonthDate, appUser));

    // Then
    assertNotNull(res);
    verify(moneyService, times(1)).readMoney(pageable, from, to, APP_USER);
    assertNull(res.getMessage());
  }

  @Test
  @DisplayName("Should not update money and return not found")
  void test3() {
    // Given
    var uuid = UUID.randomUUID();
    var moneyDto = createMoneyDto(uuid, OPERATION_DATE, AMOUNT, OPERATION_TYPE, DESCRIPTION);
    Optional<MoneyDto> updatedMoneyDto = Optional.empty();
    given(moneyService.updateMoney(uuid, moneyDto, APP_USER)).willReturn(updatedMoneyDto);

    // When
    var res = moneyController.updateMoney(uuid, moneyDto, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(moneyService, times(1)).updateMoney(uuid, moneyDto, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should update money and return ok")
  void test4() {
    // Given
    var uuid = UUID.randomUUID();
    var moneyDto = createMoneyDto(uuid, OPERATION_DATE, AMOUNT, OPERATION_TYPE, DESCRIPTION);
    var updatedMoney = Optional.of(moneyDto);
    given(moneyService.updateMoney(uuid, moneyDto, APP_USER)).willReturn(updatedMoney);

    // When
    var res = moneyController.updateMoney(uuid, moneyDto, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(moneyService, times(1)).updateMoney(uuid, moneyDto, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(moneyDto, res.getBody());
  }

  @Test
  @DisplayName("Should not delete money and throw not found")
  void test5() {
    // Given
    var uuid = UUID.randomUUID();
    doThrow(new DailyNotFoundException(Constants.ERROR_NOT_FOUND))
        .when(moneyService)
        .deleteMoney(uuid, APP_USER);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class, () -> moneyController.deleteMoney(uuid, appUser));

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(moneyService, times(1)).deleteMoney(uuid, APP_USER);
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should delete money and return no content")
  void test6() {
    // Given
    var uuid = UUID.randomUUID();

    // When
    var res = moneyController.deleteMoney(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(moneyService, times(1)).deleteMoney(uuid, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should not add tag to money and throw not found")
  void test7() {
    // Given
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();
    doThrow(new DailyNotFoundException(Constants.ERROR_NOT_FOUND))
        .when(moneyService)
        .addTagToMoney(uuid, tagUuid, APP_USER);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class,
            () -> moneyController.addTagToMoney(uuid, tagUuid, appUser));

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(moneyService, times(1)).addTagToMoney(uuid, tagUuid, APP_USER);
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should add tag to money and return no content")
  void test8() {
    // Given
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();

    // When
    var res = moneyController.addTagToMoney(uuid, tagUuid, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(moneyService, times(1)).addTagToMoney(uuid, tagUuid, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should not add tag to money because of money tag limits and return conflict")
  void test9() {
    // Given
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();
    doThrow(new DailyConflictException(Constants.ERROR_MONEY_TAGS_MAX))
        .when(moneyService)
        .addTagToMoney(uuid, tagUuid, APP_USER);

    // When
    var res =
        assertThrows(
            DailyConflictException.class,
            () -> moneyController.addTagToMoney(uuid, tagUuid, appUser));

    // Then
    assertNotNull(res);
    assertEquals(Constants.ERROR_MONEY_TAGS_MAX, res.getMessage());
  }

  @Test
  @DisplayName("Should not remove tag from money and return not found")
  void test10() {
    // Given
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();
    doThrow(new DailyNotFoundException(Constants.ERROR_NOT_FOUND))
        .when(moneyService)
        .removeTagFromMoney(uuid, tagUuid, APP_USER);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class,
            () -> moneyController.removeTagFromMoney(uuid, tagUuid, appUser));

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(moneyService, times(1)).removeTagFromMoney(uuid, tagUuid, APP_USER);
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should remove tag from money and return no content")
  void test11() {
    // Given
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();

    // When
    var res = moneyController.removeTagFromMoney(uuid, tagUuid, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(moneyService, times(1)).removeTagFromMoney(uuid, tagUuid, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }
}
