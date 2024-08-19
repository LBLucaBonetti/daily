package it.lbsoftware.daily.money;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.money.Money.OperationType;
import it.lbsoftware.daily.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public final class MoneyTestUtils {

  private MoneyTestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * {@link Money} generator; the created instance is not persisted.
   *
   * @param operationDate Operation date
   * @param amount Amount
   * @param operationType Operation type
   * @param description Description
   * @param tags Tags set
   * @param appUser The creator
   * @return The created {@link Money}
   */
  public static Money createMoney(
      final LocalDate operationDate,
      final BigDecimal amount,
      final OperationType operationType,
      final String description,
      final Set<Tag> tags,
      final AppUser appUser) {
    return Money.builder()
        .operationDate(operationDate)
        .amount(amount)
        .operationType(operationType)
        .description(description)
        .tags(tags)
        .appUser(appUser)
        .build();
  }

  /**
   * {@link MoneyDto} generator.
   *
   * @param uuid Unique identifier
   * @param operationDate Operation date
   * @param amount Amount
   * @param operationType Operation type
   * @param description Description
   * @return The created {@link MoneyDto}
   */
  public static MoneyDto createMoneyDto(
      final UUID uuid,
      final LocalDate operationDate,
      final BigDecimal amount,
      final OperationType operationType,
      final String description) {
    var moneyDto = new MoneyDto();
    moneyDto.setUuid(uuid);
    moneyDto.setOperationDate(operationDate);
    moneyDto.setAmount(amount);
    moneyDto.setOperationType(operationType);
    moneyDto.setDescription(description);
    return moneyDto;
  }
}
