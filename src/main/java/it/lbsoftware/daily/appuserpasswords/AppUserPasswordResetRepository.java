package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.appusers.AppUser;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Main {@link AppUserPasswordReset} repository. */
public interface AppUserPasswordResetRepository extends JpaRepository<AppUserPasswordReset, Long> {

  @Query("delete from AppUserPasswordReset aupr where aupr.appUser = :appUser")
  @Modifying
  void deleteByAppUser(@Param("appUser") AppUser appUser);

  @Query("select aupr from AppUserPasswordReset aupr where aupr.appUser = :appUser")
  Optional<AppUserPasswordReset> findByAppUser(@Param("appUser") AppUser appUser);

  @Query(
      "select aupr from AppUserPasswordReset aupr join fetch aupr.appUser au where au.enabled = "
          + "true and aupr.passwordResetCode = :passwordResetCode and :passwordResetThreshold < "
          + "aupr.expiredAt and aupr.usedAt is null")
  Optional<AppUserPasswordReset> findStillValidAppUserPasswordResetFetchEnabledAppUser(
      @Param("passwordResetCode") UUID passwordResetCode,
      @Param("passwordResetThreshold") LocalDateTime passwordResetThreshold);

  @Query(
      "select aupr from AppUserPasswordReset aupr where (aupr.expiredAt < :passwordResetRemovalThreshold or aupr.usedAt is not null)")
  Set<AppUserPasswordReset> findToRemove(LocalDateTime passwordResetRemovalThreshold);
}
