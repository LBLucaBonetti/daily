package it.lbsoftware.daily.appusersettings;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserSettingRepository extends JpaRepository<AppUserSetting, Long> {

  Optional<AppUserSetting> findByAppUser(AppUser appUser);
}
