package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.bases.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contains the details regarding the {@link AppUser} password reset. An {@link AppUser} who signed
 * up via e-mail and password could request this. Note that OAuth2 app users cannot request it
 * because we do not store their passwords. Each {@link AppUser} can have a single entity of this
 * type at a time.
 */
@Table(
    name = "app_user_password_reset",
    indexes = {
      @Index(name = "idx_app_user_password_reset_uuid", columnList = "uuid"),
    })
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AppUserPasswordReset extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "app_user_id",
      referencedColumnName = "id",
      updatable = false,
      nullable = false,
      unique = true)
  private AppUser appUser;

  @Column(updatable = false, nullable = false, unique = true)
  @NotNull
  private UUID passwordResetCode;

  @Column(updatable = false, nullable = false)
  @NotNull
  @Future
  private LocalDateTime expiredAt;

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
