package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.bases.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Table(indexes = @Index(name = "idx_appuser_uuid", columnList = "uuid"))
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AppUser extends BaseEntity {

  @Column(updatable = false, nullable = false, unique = true)
  @NotBlank
  /*
   Okta unique identifier for this user
  */
  private String uid;

  @Column(nullable = false, unique = true)
  @Email
  @NotNull
  @Setter
  /*
   Okta registration email for this user
  */
  private String email;

  @Override
  public int hashCode() {
    HashCodeBuilder hcb = new HashCodeBuilder();
    hcb.append(uid);
    return hcb.toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof AppUser)) return false;
    AppUser that = (AppUser) obj;
    EqualsBuilder eb = new EqualsBuilder();
    eb.append(uid, that.uid);
    return eb.isEquals();
  }
}
