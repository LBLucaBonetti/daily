package it.lbsoftware.daily.appusersactivations;

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

@Table(
    name = "app_user_activation",
    indexes = {
      @Index(name = "idx_app_user_activation_uuid", columnList = "uuid"),
    })
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
/*
 The AppUserActivation contains the details regarding the AppUser activation. An AppUser who
 signed up via email and password should activate its account before being able to log in. Note
 that OAuth2 app users are not required to activate their account as the OAuth2 provider is
 trusted
*/
public class AppUserActivation extends BaseEntity {

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
  private UUID activationCode;

  @Column(updatable = false, nullable = false)
  @NotNull
  @Future
  private LocalDateTime expiredAt;

  private LocalDateTime activatedAt;

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
