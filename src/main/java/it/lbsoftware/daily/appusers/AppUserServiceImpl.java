package it.lbsoftware.daily.appusers;

//import it.lbsoftware.daily.emails.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    //private final EmailService emailService;

    @Override
    public void checkAppUserPresence(@NotNull JwtAuthenticationToken jwtAuthenticationToken) {
        System.out.println("checkAppUserPresence called!");
        String uid = (String) jwtAuthenticationToken.getTokenAttributes().get("uid");
        Optional<AppUser> appUserOptional = appUserRepository.findByUid(uid);
        LocalDateTime now = LocalDateTime.now();
        if (appUserOptional.isEmpty()) {
            System.out.println("user does not exist, creating...");
            // By default, the name maps to the sub claim according to Spring Security docs
            String email = jwtAuthenticationToken.getName();
            AppUser newUser = AppUser.builder()
                    .uid(uid)
                    .email(email)
                    .registrationDateTime(now)
                    .lastAccessDateTime(now)
                    .build();
            appUserRepository.save(newUser);
            //emailService.send(newUser.getEmail(), "Welcome!", "Welcome to Daily :)");
        } else {
            System.out.println("user already exists, updating last access...");
            AppUser appUser = appUserOptional.get();
            appUser.setLastAccessDateTime(now);
            appUserRepository.save(appUser);
        }
    }

}
