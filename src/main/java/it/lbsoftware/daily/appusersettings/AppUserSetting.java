package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.config.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(
    name = "app_user_setting",
    indexes = {
      @Index(name = "idx_app_user_setting_uuid", columnList = "uuid"),
      @Index(name = "idx_app_user_setting_appuser", columnList = "app_user")
    })
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AppUserSetting extends BaseEntity {

  @Column(name = "app_user", updatable = false, nullable = false, unique = true)
  @NotNull
  private UUID appUser;

  @Column(name = "lang", nullable = false)
  @NotBlank
  @Pattern(regexp = Constants.APP_USER_LANG_REGEXP)
  @Builder.Default
  private String lang = "en-US";

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
