package it.lbsoftware.daily.money;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.money.Money.OperationType;
import it.lbsoftware.daily.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public final class MoneyTestUtils {

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
}
