package it.lbsoftware.daily.appusers;

//import it.lbsoftware.daily.emails.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
//    private final EmailService emailService;

    @Override
    public void checkAppUserRegistration(JwtAuthenticationToken jwtAuthenticationToken) {
        String uid = (String) jwtAuthenticationToken.getTokenAttributes().get("uid");
        Optional<AppUser> appUserOptional = appUserRepository.findByUid(uid);
        LocalDateTime now = LocalDateTime.now();
        // By default, the name maps to the sub claim according to Spring Security docs
        String email = jwtAuthenticationToken.getName();
        if (appUserOptional.isEmpty()) {
//            System.out.println("User does not exist, creating...");
            AppUser newUser = AppUser.builder()
                    .uid(uid)
                    .email(email)
                    .registrationDateTime(now)
                    .lastAccessDateTime(now)
                    .build();
            appUserRepository.save(newUser);
//            emailService.send(newUser.getEmail(), "Welcome!", "Welcome to Daily :)");
        } else {
//            System.out.println("User already exists, updating information...");
            AppUser appUser = appUserOptional.get();
            if (!appUser.getEmail().equals(email)) {
                // User changed email address, update accordingly
                appUser.setEmail(email);
            }
            appUser.setLastAccessDateTime(now);
            appUserRepository.save(appUser);
        }
    }

}
