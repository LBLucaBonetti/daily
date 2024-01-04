package it.lbsoftware.daily.appusers;

import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    final String usernameToSearch =
        Optional.ofNullable(username)
            .filter(StringUtils::isNotBlank)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "AppUser with a null or blank e-mail does not exist"));

    AppUser appUser =
        appUserRepository
            .findByEmailIgnoreCaseAndAuthProvider(usernameToSearch, AuthProvider.DAILY)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "AppUser with email " + usernameToSearch + " not found!"));

    return new AppUserDetails(appUser);
  }
}
