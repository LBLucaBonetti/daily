package it.lbsoftware.daily.money;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.tags.Tag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** The money entity, representing either an income, an outcome, ... */
@Table(
    indexes = {
      @Index(name = "idx_money_uuid", columnList = "uuid"),
    })
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Money extends BaseEntity {

  /** The date this money refers to, such as the withdrawal date of a certain amount. */
  @Column(nullable = false)
  private LocalDate operationDate;

  /** The amount of money. */
  @NotNull private BigDecimal amount;

  /** The money operation type. */
  @Enumerated(EnumType.STRING)
  @Column(name = "operation_type", nullable = false, length = Constants.MONEY_OPERATION_TYPE_MAX)
  private OperationType operationType;

  /** A text describing the money operation. */
  @Column(length = Constants.MONEY_DESCRIPTION_MAX)
  private String description;

  /** Tags set for this money. */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "moneyTag",
      joinColumns = @JoinColumn(name = "moneyId"),
      inverseJoinColumns = @JoinColumn(name = "tagId"))
  @Builder.Default
  private Set<Tag> tags = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "app_user_id",
      updatable = false,
      nullable = false,
      referencedColumnName = "id")
  @NotNull
  private AppUser appUser;

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /** The type of money: income, outcome, ... */
  public enum OperationType {
    INCOME,
    OUTCOME,
  }
}
