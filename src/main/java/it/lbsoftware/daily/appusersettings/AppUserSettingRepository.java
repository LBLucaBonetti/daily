package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Main {@link AppUserSetting} repository. */
public interface AppUserSettingRepository extends JpaRepository<AppUserSetting, Long> {

  Optional<AppUserSetting> findByAppUser(AppUser appUser);

  @Query("delete from AppUserSetting aus where aus.appUser = :appUser")
  @Modifying
  void deleteByAppUser(@Param("appUser") AppUser appUser);
}
