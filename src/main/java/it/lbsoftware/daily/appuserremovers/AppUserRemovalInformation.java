package it.lbsoftware.daily.appuserremovers;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.bases.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contains the details regarding the {@link AppUser} removal. An {@link AppUser} whose removal
 * information object has certain characteristics could be notified of a pending scheduled removal
 * for inactivity or completely removed, if already notified before; to preserve users that could
 * not be notified up until a certain threshold, failures sending the removal notification are
 * counted as well.
 */
@Table(
    name = "app_user_removal_information",
    indexes = {
      @Index(name = "idx_app_user_removal_information_uuid", columnList = "uuid"),
    })
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AppUserRemovalInformation extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "app_user_id",
      referencedColumnName = "id",
      updatable = false,
      nullable = false,
      unique = true)
  private AppUser appUser;

  /**
   * Starts null and is filled when the removal notification is correctly sent or when the failures
   * have reached a certain threshold.
   */
  private LocalDateTime notifiedAt;

  /** Starts at 0 and counts every unsuccessful attempt to send the removal notification. */
  private int failures;

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
