package it.lbsoftware.daily.appusers;

import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** A {@link UserDetails} custom implementation to handle {@link AppUser} entities. */
@RequiredArgsConstructor
public class AppUserDetails implements UserDetails {

  private final transient AppUser appUser;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return appUser.getPassword();
  }

  @Override
  public String getUsername() {
    return appUser.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return appUser.isEnabled();
  }

  public String getFullname() {
    return appUser.getFirstName() + " " + appUser.getLastName();
  }
}
