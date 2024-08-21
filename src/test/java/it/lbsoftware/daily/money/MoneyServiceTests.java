package it.lbsoftware.daily.money;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static it.lbsoftware.daily.money.MoneyTestUtils.createMoney;
import static it.lbsoftware.daily.money.MoneyTestUtils.createMoneyDto;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.money.Money.OperationType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class MoneyServiceTests extends DailyAbstractUnitTests {

  private static final String EMAIL = "appuser@email.com";
  private static final UUID UNIQUE_ID = UUID.randomUUID();
  private static final AppUser APP_USER = createAppUser(UNIQUE_ID, EMAIL);
  @Mock private Pageable pageable;
  @Mock private MoneyRepository moneyRepository;
  @Mock private MoneyDtoMapper moneyDtoMapper;
  private MoneyService moneyService;

  private static Stream<Arguments> test1() {
    // From, to, appUser
    var now = LocalDate.now();
    var from = now.with(firstDayOfMonth());
    var to = now.with(lastDayOfMonth());
    return Stream.of(
        arguments(null, null, null),
        arguments(null, null, APP_USER),
        arguments(null, to, null),
        arguments(null, to, APP_USER),
        arguments(from, null, null),
        arguments(from, null, APP_USER),
        arguments(from, to, null));
  }

  @BeforeEach
  void beforeEach() {
    moneyService = new MoneyService(moneyRepository, moneyDtoMapper);
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when read money with null arguments")
  void test1(LocalDate from, LocalDate to, AppUser appUser) {
    assertThrows(
        IllegalArgumentException.class, () -> moneyService.readMoney(pageable, from, to, appUser));
  }

  @Test
  @DisplayName("Should not read money and return empty list")
  void test2() {
    // Given
    Page<Money> money = Page.empty();
    var now = LocalDate.now();
    var from = now.with(firstDayOfMonth());
    var to = now.with(lastDayOfMonth());
    given(moneyRepository.findByOperationDateBetweenAndAppUser(pageable, from, to, APP_USER))
        .willReturn(money);

    // When
    Page<MoneyDto> res = moneyService.readMoney(pageable, from, to, APP_USER);

    // Then
    verify(moneyRepository, times(1))
        .findByOperationDateBetweenAndAppUser(pageable, from, to, APP_USER);
    verify(moneyDtoMapper, times(0)).convertToDto(any());
    assertEquals(Page.empty(), res);
  }

  @Test
  @DisplayName("Should read money and return money list")
  void test3() {
    // Given
    var operationDate = LocalDate.now();
    var amount = BigDecimal.TEN;
    var description = "description";
    var from = operationDate.with(firstDayOfMonth());
    var to = operationDate.with(lastDayOfMonth());
    Money money =
        createMoney(
            operationDate,
            amount,
            OperationType.INCOME,
            description,
            Collections.emptySet(),
            APP_USER);
    MoneyDto moneyDto =
        createMoneyDto(UUID.randomUUID(), operationDate, amount, OperationType.INCOME, description);
    Page<Money> moneyPage = new PageImpl<>(List.of(money));
    given(moneyRepository.findByOperationDateBetweenAndAppUser(pageable, from, to, APP_USER))
        .willReturn(moneyPage);
    given(moneyDtoMapper.convertToDto(money)).willReturn(moneyDto);

    // When
    Page<MoneyDto> res = moneyService.readMoney(pageable, from, to, APP_USER);

    // Then
    verify(moneyRepository, times(1))
        .findByOperationDateBetweenAndAppUser(pageable, from, to, APP_USER);
    verify(moneyDtoMapper, times(1)).convertToDto(money);
    assertTrue(res.get().findFirst().isPresent());
    assertEquals(moneyDto, res.get().findFirst().get());
  }
}
