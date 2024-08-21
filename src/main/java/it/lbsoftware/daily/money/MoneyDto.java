package it.lbsoftware.daily.money;

import it.lbsoftware.daily.bases.BaseDto;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.money.Money.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** {@link Money} dto. */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class MoneyDto extends BaseDto {

  @NotNull private LocalDate operationDate;

  @NotNull private BigDecimal amount;

  @NotNull private OperationType operationType;

  @Size(max = Constants.MONEY_DESCRIPTION_MAX)
  private String description;
}
