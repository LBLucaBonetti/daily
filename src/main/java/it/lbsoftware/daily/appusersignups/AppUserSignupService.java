package it.lbsoftware.daily.appusersignups;

import it.lbsoftware.daily.appusers.AppUserDto;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

/** Front-facing service to deal with {@link it.lbsoftware.daily.appusers.AppUser} sign up. */
public interface AppUserSignupService {

  /**
   * Tries to sign a new {@code AppUser} up with the auth provider {@code DAILY}. This method should
   * avoid leaking sensible information that could lead to security vulnerabilities such as user
   * enumeration.
   *
   * @param appUserDto The {@code AppUser} data
   * @param bindingResult The result of validating the provided {@code AppUser} data
   * @param model The ui model
   */
  void signup(AppUserDto appUserDto, BindingResult bindingResult, Model model);
}
