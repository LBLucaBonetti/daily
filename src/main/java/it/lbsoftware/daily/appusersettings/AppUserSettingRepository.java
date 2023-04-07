package it.lbsoftware.daily.appusersettings;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserSettingRepository extends JpaRepository<AppUserSetting, Long> {

  Optional<AppUserSetting> findByAppUser(UUID appUser);
}
