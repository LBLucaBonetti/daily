package it.lbsoftware.daily.appusercreations;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserDto;
import java.util.Optional;
import java.util.UUID;

/** Service to create {@link AppUser} entities. */
public interface AppUserCreationService {

  /**
   * Creates a new {@code AppUser} with the provided information; its auth provider will be {@code
   * DAILY}. This method should also create the required records that need to be initialized when
   * creating a new {@code AppUser}.
   *
   * @param appUserDto The {@code AppUser} data
   * @return When the operation is successful, the activation code that needs to be sent via e-mail
   *     to the user; an empty {@code Optional} on failure
   */
  Optional<UUID> createDailyAppUser(AppUserDto appUserDto);

  /**
   * Creates a new {@code AppUser} with the provided information; its auth provider will be assigned
   * according to the authProvider parameter; if the {@code AppUser} is already present, it will be
   * updated with the provided data.
   *
   * @param appUserDto The {@code AppUser} data
   * @param authProvider The auth provider to assign
   * @param authProviderId The unique identifier to distinct this OAuth2 user among the others in
   *     the specific OAuth2 domain; there should not be multiple users with the same authProviderId
   *     within that OAuth2 provider
   */
  void createOrUpdateOauth2AppUser(
      AppUserDto appUserDto, AppUser.AuthProvider authProvider, String authProviderId);
}
