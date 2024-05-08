package it.lbsoftware.daily.appuserremovers;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppUserRemovalInformationRepository
    extends JpaRepository<AppUserRemovalInformation, Long> {

  Optional<AppUserRemovalInformation> findByAppUser(AppUser appUser);

  @Query("delete from AppUserRemovalInformation auri where auri.appUser = :appUser")
  @Modifying
  void deleteByAppUser(@Param("appUser") AppUser appUser);
}
