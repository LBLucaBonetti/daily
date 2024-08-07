package it.lbsoftware.daily.appuseractivations;

import it.lbsoftware.daily.appusers.AppUser;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Main {@link AppUserActivation} repository. */
public interface AppUserActivationRepository extends JpaRepository<AppUserActivation, Long> {

  @Query(
      "select aua from AppUserActivation aua join fetch aua.appUser where aua.activationCode = "
          + ":activationCode and aua.activatedAt is null and :activationThreshold < aua.expiredAt")
  Optional<AppUserActivation> findNonActivatedAndStillValidAppUserActivationFetchAppUser(
      @Param("activationCode") UUID activationCode,
      @Param("activationThreshold") LocalDateTime activationThreshold);

  @Query("delete from AppUserActivation aua where aua.appUser = :appUser")
  @Modifying
  void deleteByAppUser(@Param("appUser") AppUser appUser);
}
