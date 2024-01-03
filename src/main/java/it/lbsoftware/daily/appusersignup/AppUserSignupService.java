package it.lbsoftware.daily.appusersignup;

import it.lbsoftware.daily.appusers.AppUserDto;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

public interface AppUserSignupService {

  /**
   * Tries to sign a new AppUser up with the auth provider DAILY
   *
   * @param appUserDto The AppUser data
   * @param bindingResult The result of validating the provided AppUser data
   * @param model The ui model
   * @return The view name to show
   */
  String signup(AppUserDto appUserDto, BindingResult bindingResult, Model model);
}
