package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  Optional<AppUser> findByEmailIgnoreCase(String email);

  Optional<AppUser> findByEmailIgnoreCaseAndAuthProvider(String email, AuthProvider authProvider);

  Optional<AppUser> findByAuthProviderIdAndAuthProvider(
      String authProviderId, AuthProvider authProvider);

  Optional<AppUser> findByUuidAndAuthProvider(UUID uuid, AuthProvider authProvider);
}
