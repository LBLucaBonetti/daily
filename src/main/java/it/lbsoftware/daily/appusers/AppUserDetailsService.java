package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

  private final AppUserRepository appUserRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser appUser =
        appUserRepository
            .findByEmailIgnoreCaseAndAuthProvider(username, AuthProvider.DAILY)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "AppUser with email " + username + " not found!"));

    return new AppUserDetails(appUser);
  }
}
