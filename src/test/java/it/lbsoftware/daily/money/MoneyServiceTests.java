package it.lbsoftware.daily.money;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static it.lbsoftware.daily.money.MoneyTestUtils.createMoney;
import static it.lbsoftware.daily.money.MoneyTestUtils.createMoneyDto;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exceptions.DailyConflictException;
import it.lbsoftware.daily.exceptions.DailyNotFoundException;
import it.lbsoftware.daily.money.Money.OperationType;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

  private static final LocalDate OPERATION_DATE = LocalDate.now();
  private static final BigDecimal AMOUNT = BigDecimal.TEN;
  private static final OperationType OPERATION_TYPE = OperationType.INCOME;
  private static final String DESCRIPTION = "description";
  private static final String EMAIL = "appuser@email.com";
  private static final UUID UNIQUE_ID = UUID.randomUUID();
  private static final AppUser APP_USER = createAppUser(UNIQUE_ID, EMAIL);
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  @Mock private Pageable pageable;
  @Mock private MoneyRepository moneyRepository;
  @Mock private MoneyDtoMapper moneyDtoMapper;
  @Mock private TagRepository tagRepository;
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

  private static Stream<Arguments> test6() {
    // Uuid, money, appUser
    UUID uuid = UUID.randomUUID();
    MoneyDto money =
        createMoneyDto(uuid, LocalDate.now(), BigDecimal.TEN, OperationType.INCOME, "description");
    return Stream.of(
        arguments(null, null, null),
        arguments(null, null, APP_USER),
        arguments(null, money, null),
        arguments(null, money, APP_USER),
        arguments(uuid, null, null),
        arguments(uuid, null, APP_USER),
        arguments(uuid, money, null));
  }

  private static Stream<Arguments> test7() {
    // Uuid, appUser
    UUID uuid = UUID.randomUUID();
    return Stream.of(arguments(null, null), arguments(null, APP_USER), arguments(uuid, null));
  }

  private static Stream<Arguments> test13() {
    // Uuid, tagUuid, appUser
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    return Stream.of(
        arguments(null, null, null),
        arguments(null, null, APP_USER),
        arguments(null, tagUuid, null),
        arguments(null, tagUuid, APP_USER),
        arguments(uuid, null, null),
        arguments(uuid, null, APP_USER),
        arguments(uuid, tagUuid, null));
  }

  private static Stream<Arguments> test18() {
    // Uuid, tagUuid, appUser
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    return Stream.of(
        arguments(null, null, null),
        arguments(null, null, APP_USER),
        arguments(null, tagUuid, null),
        arguments(null, tagUuid, APP_USER),
        arguments(uuid, null, null),
        arguments(uuid, null, APP_USER),
        arguments(uuid, tagUuid, null));
  }

  @BeforeEach
  void beforeEach() {
    moneyService = new MoneyService(moneyRepository, moneyDtoMapper, tagRepository);
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

  @Test
  @DisplayName("Should not update money and return empty optional")
  void test4() {
    // Given
    Optional<Money> moneyOptional = Optional.empty();
    var uuid = UUID.randomUUID();
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(moneyOptional);

    // When
    var res =
        moneyService.updateMoney(
            uuid,
            createMoneyDto(
                uuid, LocalDate.now(), BigDecimal.TEN, OperationType.INCOME, "description"),
            APP_USER);

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(moneyRepository, times(0)).save(any());
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName("Should update money and return money optional")
  void test5() {
    // Given
    var operationDate = LocalDate.now();
    var amount = BigDecimal.TEN;
    var description = "description";
    var otherDescription = "otherDescription";
    Money prevMoney =
        createMoney(
            operationDate,
            amount,
            OperationType.INCOME,
            description,
            Collections.emptySet(),
            APP_USER);
    Money updatedMoney =
        createMoney(
            operationDate,
            amount,
            OperationType.INCOME,
            otherDescription,
            Collections.emptySet(),
            APP_USER);
    var uuid = UUID.randomUUID();
    var updatedMoneyDto =
        createMoneyDto(uuid, operationDate, amount, OperationType.INCOME, otherDescription);
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(Optional.of(prevMoney));
    given(moneyRepository.saveAndFlush(prevMoney)).willReturn(updatedMoney);
    given(moneyDtoMapper.convertToDto(updatedMoney)).willReturn(updatedMoneyDto);

    // When
    Optional<MoneyDto> res = moneyService.updateMoney(uuid, updatedMoneyDto, APP_USER);

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(moneyRepository, times(1)).saveAndFlush(prevMoney);
    assertEquals(res, Optional.of(updatedMoneyDto));
    assertTrue(res.isPresent());
    assertEquals(otherDescription, res.get().getDescription());
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when update money with null argument")
  void test6(UUID uuid, MoneyDto money, AppUser appUser) {
    assertThrows(
        IllegalArgumentException.class, () -> moneyService.updateMoney(uuid, money, appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when delete money with null argument")
  void test7(UUID uuid, AppUser appUser) {
    assertThrows(IllegalArgumentException.class, () -> moneyService.deleteMoney(uuid, appUser));
  }

  @Test
  @DisplayName("Should not delete money and throw")
  void test8() {
    // Given
    Optional<Money> moneyOptional = Optional.empty();
    var uuid = UUID.randomUUID();
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(moneyOptional);

    // When
    var res =
        assertThrows(DailyNotFoundException.class, () -> moneyService.deleteMoney(uuid, APP_USER));

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(moneyRepository, times(0)).delete(any());
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should delete money")
  void test9() {
    // Given
    Optional<Money> moneyOptional =
        Optional.of(
            createMoney(
                OPERATION_DATE,
                AMOUNT,
                OPERATION_TYPE,
                DESCRIPTION,
                Collections.emptySet(),
                APP_USER));
    var uuid = UUID.randomUUID();
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(moneyOptional);

    // When
    assertDoesNotThrow(() -> moneyService.deleteMoney(uuid, APP_USER));

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(moneyRepository, times(1)).delete(moneyOptional.get());
  }

  @Test
  @DisplayName("Should not add tag to money because of money not found")
  void test10() {
    // Given
    Optional<Money> moneyOptional = Optional.empty();
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(moneyOptional);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class,
            () -> moneyService.addTagToMoney(uuid, tagUuid, APP_USER));

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(0)).findByUuidAndAppUser(any(), any());
    verify(moneyRepository, times(0)).save(any());
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should not add tag to money because of tag not found")
  void test11() {
    // Given
    var moneyOptional =
        Optional.of(
            createMoney(
                OPERATION_DATE,
                AMOUNT,
                OPERATION_TYPE,
                DESCRIPTION,
                Collections.emptySet(),
                APP_USER));
    Optional<Tag> tagOptional = Optional.empty();
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(moneyOptional);
    given(tagRepository.findByUuidAndAppUser(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class,
            () -> moneyService.addTagToMoney(uuid, tagUuid, APP_USER));

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).findByUuidAndAppUser(tagUuid, APP_USER);
    verify(moneyRepository, times(0)).save(any());
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should add tag to money")
  void test12() {
    // Given
    var money =
        createMoney(OPERATION_DATE, AMOUNT, OPERATION_TYPE, DESCRIPTION, new HashSet<>(), APP_USER);
    var moneyOptional = Optional.of(money);
    var tag = createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER);
    var tagOptional = Optional.of(tag);
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(moneyOptional);
    given(tagRepository.findByUuidAndAppUser(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    assertDoesNotThrow(() -> moneyService.addTagToMoney(uuid, tagUuid, APP_USER));

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).findByUuidAndAppUser(tagUuid, APP_USER);
    assertTrue(money.getTags().contains(tag));
    assertTrue(tag.getMoney().contains(money));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when add tag to money with null argument")
  void test13(UUID uuid, UUID tagUuid, AppUser appUser) {
    assertThrows(
        IllegalArgumentException.class, () -> moneyService.addTagToMoney(uuid, tagUuid, appUser));
  }

  @Test
  @DisplayName("Should not add tag to money because of money tag limits and throw")
  void test14() {
    // Given
    var money =
        createMoney(OPERATION_DATE, AMOUNT, OPERATION_TYPE, DESCRIPTION, new HashSet<>(), APP_USER);
    var moneyOptional = Optional.of(money);
    var tagOptional = Optional.of(createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER));
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(moneyOptional);
    given(tagRepository.findByUuidAndAppUser(tagUuid, APP_USER)).willReturn(tagOptional);
    createTag("name1", "#123456", new HashSet<>(), APP_USER).addToMoney(money);
    createTag("name2", "#234567", new HashSet<>(), APP_USER).addToMoney(money);
    createTag("name3", "#345678", new HashSet<>(), APP_USER).addToMoney(money);
    createTag("name4", "#456789", new HashSet<>(), APP_USER).addToMoney(money);
    createTag("name5", "#567890", new HashSet<>(), APP_USER).addToMoney(money);

    // When
    var res =
        assertThrows(
            DailyConflictException.class,
            () -> moneyService.addTagToMoney(uuid, tagUuid, APP_USER));

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).findByUuidAndAppUser(tagUuid, APP_USER);
    verify(moneyRepository, times(0)).save(any());
    assertEquals(Constants.ERROR_MONEY_TAGS_MAX, res.getMessage());
  }

  @Test
  @DisplayName("Should not remove tag from money because of money not found")
  void test15() {
    // Given
    Optional<Money> moneyOptional = Optional.empty();
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(moneyOptional);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class,
            () -> moneyService.removeTagFromMoney(uuid, tagUuid, APP_USER));

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(0)).findByUuidAndAppUser(any(), any());
    verify(moneyRepository, times(0)).save(any());
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should not remove tag from money because of tag not found")
  void test16() {
    // Given
    var moneyOptional =
        Optional.of(
            createMoney(
                OPERATION_DATE,
                AMOUNT,
                OPERATION_TYPE,
                DESCRIPTION,
                Collections.emptySet(),
                APP_USER));
    Optional<Tag> tagOptional = Optional.empty();
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(moneyOptional);
    given(tagRepository.findByUuidAndAppUser(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class,
            () -> moneyService.removeTagFromMoney(uuid, tagUuid, APP_USER));

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).findByUuidAndAppUser(tagUuid, APP_USER);
    verify(moneyRepository, times(0)).save(any());
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should remove tag from money")
  void test17() {
    // Given
    var money =
        createMoney(OPERATION_DATE, AMOUNT, OPERATION_TYPE, DESCRIPTION, new HashSet<>(), APP_USER);
    var moneyOptional = Optional.of(money);
    var tag = createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER);
    var tagOptional = Optional.of(tag);
    tag.addToMoney(money);
    var uuid = UUID.randomUUID();
    var tagUuid = UUID.randomUUID();
    given(moneyRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(moneyOptional);
    given(tagRepository.findByUuidAndAppUser(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    assertDoesNotThrow(() -> moneyService.removeTagFromMoney(uuid, tagUuid, APP_USER));

    // Then
    verify(moneyRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).findByUuidAndAppUser(tagUuid, APP_USER);
    assertFalse(money.getTags().contains(tag));
    assertFalse(tag.getMoney().contains(money));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when remove tag from money with null argument")
  void test18(UUID uuid, UUID tagUuid, AppUser appUser) {
    assertThrows(
        IllegalArgumentException.class,
        () -> moneyService.removeTagFromMoney(uuid, tagUuid, appUser));
  }
}
