package it.lbsoftware.daily.appusersignups;

import it.lbsoftware.daily.appusers.AppUserDto;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

public interface AppUserSignupService {

  /**
   * Tries to sign a new {@code AppUser} up with the auth provider {@code DAILY}
   *
   * @param appUserDto The {@code AppUser} data
   * @param bindingResult The result of validating the provided {@code AppUser} data
   * @param model The ui model
   */
  void signup(AppUserDto appUserDto, BindingResult bindingResult, Model model);
}
