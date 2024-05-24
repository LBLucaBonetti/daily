package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Main repository to handle {@link AppUser} entities. */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  Optional<AppUser> findByEmailIgnoreCase(String email);

  Optional<AppUser> findByEmailIgnoreCaseAndAuthProvider(String email, AuthProvider authProvider);

  Optional<AppUser> findByAuthProviderIdAndAuthProvider(
      String authProviderId, AuthProvider authProvider);

  @Query(
      """
        select au from AppUser au
        where au.createdAt <= :removalNotificationThreshold and
        (au.lastLoginAt is null or au.lastLoginAt <= :removalNotificationThreshold) and
        (exists
          (
            select auri.appUser from AppUserRemovalInformation auri where auri.appUser = au and
            auri.notifiedAt is null
          )
        )
      """)
  Set<AppUser> findToNotifyForRemoval(
      @Param("removalNotificationThreshold") LocalDateTime removalNotificationThreshold);

  @Query(
      """
        select au from AppUser au
        where au.createdAt <= :removalThreshold and
        (au.lastLoginAt is null or au.lastLoginAt <= :removalThreshold) and
        (exists
          (
            select auri.appUser from AppUserRemovalInformation auri where auri.appUser = au and
            auri.notifiedAt is not null and
            auri.notifiedAt <= :removalNotificationThreshold
          )
        )
      """)
  Set<AppUser> findToRemove(
      @Param("removalThreshold") LocalDateTime removalThreshold,
      @Param("removalNotificationThreshold") LocalDateTime removalNotificationThreshold);
}
