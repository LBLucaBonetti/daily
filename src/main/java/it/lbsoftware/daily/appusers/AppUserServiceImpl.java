package it.lbsoftware.daily.appusers;

//import it.lbsoftware.daily.emails.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
//    private final EmailService emailService;

    @Override
    public void checkAppUserRegistration(JwtAuthenticationToken jwtAuthenticationToken) {
        AppUser appUser;
        try {
            appUser = getAppUserFromToken(jwtAuthenticationToken);
        } catch (EntityNotFoundException e) {
            appUser = null;
        }
        LocalDateTime now = LocalDateTime.now();
        // By default, the name maps to the sub claim according to Spring Security docs
        String email = jwtAuthenticationToken.getName();
        if (appUser == null) {
            AppUser newUser = AppUser.builder()
                    .uid((String) jwtAuthenticationToken.getTokenAttributes().get("uid"))
                    .email(email)
                    .registrationDateTime(now)
                    .lastAccessDateTime(now)
                    .build();
            appUserRepository.save(newUser);
//            emailService.send(newUser.getEmail(), "Welcome!", "Welcome to Daily :)");
        } else {
            if (!appUser.getEmail().equals(email)) {
                // User changed email address, update accordingly
                appUser.setEmail(email);
            }
            appUser.setLastAccessDateTime(now);
            appUserRepository.save(appUser);
        }
    }

    @Override
    public AppUser getAppUserFromToken(JwtAuthenticationToken jwtAuthenticationToken) throws EntityNotFoundException {
        if (jwtAuthenticationToken == null) {
            throw new EntityNotFoundException();
        }
        String uid = (String) jwtAuthenticationToken.getTokenAttributes().get("uid");
        if (uid == null) {
            throw new EntityNotFoundException();
        }
        Optional<AppUser> appUserOptional = appUserRepository.findByUid(uid);
        if (appUserOptional.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return appUserOptional.get();
    }

}
