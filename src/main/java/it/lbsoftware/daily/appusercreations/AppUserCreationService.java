package it.lbsoftware.daily.appusercreations;

import it.lbsoftware.daily.appusers.AppUserDto;
import java.util.Optional;

public interface AppUserCreationService {

  /**
   * Creates a new {@code AppUser} with the provided information; its auth provider will be {@code
   * DAILY}. This method should also create the required records that need to be initialized when
   * creating a new {@code AppUser}
   *
   * @param appUserDto The {@code AppUser} data
   * @return When the operation is successful, the activation code that needs to be sent via e-mail
   *     to the user; an empty {@code Optional} on failure
   */
  Optional<String> createDailyAppUser(AppUserDto appUserDto);
}
