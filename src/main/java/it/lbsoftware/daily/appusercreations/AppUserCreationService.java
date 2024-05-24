package it.lbsoftware.daily.appusercreations;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserDto;
import java.util.Optional;
import java.util.UUID;

/** Service to create {@link AppUser} entities. */
public interface AppUserCreationService {

  /**
   * Creates a new {@link AppUser} with the provided information; its auth provider will be {@code
   * DAILY}. This method should also create the required records that need to be initialized when
   * creating a new {@link AppUser}.
   *
   * @param appUserDto The {@link AppUser} data
   * @return When the operation is successful, the activation code that needs to be sent via e-mail
   *     to the user; an empty value on failure
   */
  Optional<UUID> createDailyAppUser(AppUserDto appUserDto);

  /**
   * Creates a new {@link AppUser} with the provided information; its auth provider will be assigned
   * according to the authProvider parameter; if the {@link AppUser} is already present, it will be
   * updated with the provided data.
   *
   * @param appUserDto The {@link AppUser} data
   * @param authProvider The auth provider to assign
   * @param authProviderId The unique identifier to distinct this OAuth2 user among the others in
   *     the specific OAuth2 domain; there should not be multiple users with the same authProviderId
   *     within that OAuth2 provider
   */
  void createOrUpdateOauth2AppUser(
      AppUserDto appUserDto, AppUser.AuthProvider authProvider, String authProviderId);
}
