package it.lbsoftware.daily.appuserpasswords;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.Optional;
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
}
