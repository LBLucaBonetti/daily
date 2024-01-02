package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.bases.BaseEntity;
import it.lbsoftware.daily.config.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Table(
    name = "app_user",
    indexes = {
      @Index(name = "idx_app_user_uuid", columnList = "uuid"),
    },
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uc_appuser_auth_provider_id_auth_provider",
          columnNames = {"auth_provider_id", "auth_provider"})
    })
@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AppUser extends BaseEntity {

  @Column(name = "auth_provider_id", updatable = false, columnDefinition = "TEXT")
  private String authProviderId;

  @Enumerated(EnumType.STRING)
  @Column(
      name = "auth_provider",
      updatable = false,
      nullable = false,
      length = Constants.APP_USER_AUTH_PROVIDER_MAX)
  private AuthProvider authProvider;

  @Column(nullable = false, unique = true, length = Constants.APP_USER_EMAIL_MAX)
  @Email
  @NotNull
  private String email;

  @Column(columnDefinition = "TEXT")
  private String password;

  @Column(name = "first_name", length = Constants.APP_USER_FIRST_NAME_MAX)
  private String firstName;

  @Column(name = "last_name", length = Constants.APP_USER_LAST_NAME_MAX)
  private String lastName;

  @Column(nullable = false)
  private boolean enabled;

  public AppUser(
      String authProviderId,
      AuthProvider authProvider,
      String email,
      String password,
      String firstName,
      String lastName,
      boolean enabled) {
    this.authProviderId = authProviderId;
    this.authProvider = authProvider;
    setEmail(email); // Custom logic there
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.enabled = enabled;
  }

  public String getEmail() {
    return StringUtils.toRootLowerCase(this.email);
  }

  public void setEmail(String email) {
    this.email = StringUtils.toRootLowerCase(email);
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  public enum AuthProvider {
    DAILY,
    GOOGLE
  }

  public static class AppUserBuilder {

    private String email;

    public AppUserBuilder email(String email) {
      this.email = StringUtils.toRootLowerCase(email);
      return this;
    }
  }
}
