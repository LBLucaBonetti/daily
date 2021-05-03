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
        Optional<AppUser> appUserOptional = getAppUserFromToken(jwtAuthenticationToken);

        LocalDateTime now = LocalDateTime.now();
        // By default, the name maps to the sub claim according to Spring Security docs
        String email = jwtAuthenticationToken.getName();
        if (appUserOptional.isEmpty()) {
            AppUser newUser = AppUser.builder()
                    .uid((String) jwtAuthenticationToken.getTokenAttributes().get("uid"))
                    .email(email)
                    .registrationDateTime(now)
                    .lastAccessDateTime(now)
                    .build();
            appUserRepository.save(newUser);
//            emailService.send(newUser.getEmail(), "Welcome!", "Welcome to Daily :)");
        } else {
            AppUser appUser = appUserOptional.get();
            if (!appUser.getEmail().equals(email)) {
                // User changed email address, update accordingly
                appUser.setEmail(email);
            }
            appUser.setLastAccessDateTime(now);
            appUserRepository.save(appUser);
        }
    }

    @Override
    public Optional<AppUser> getAppUserFromToken(JwtAuthenticationToken jwtAuthenticationToken) {
        if (jwtAuthenticationToken == null) {
            return Optional.empty();
        }
        String uid = (String) jwtAuthenticationToken.getTokenAttributes().get("uid");
        if (uid == null) {
            return Optional.empty();
        }
        Optional<AppUser> appUserOptional = appUserRepository.findByUid(uid);

        return appUserOptional;
    }

}
