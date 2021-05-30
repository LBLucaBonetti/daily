package it.lbsoftware.daily.appusers;

//import it.lbsoftware.daily.emails.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
//    private final EmailService emailService;

    @Override
    public void setAppUserToToken(JwtAuthenticationToken jwtAuthenticationToken) {
        String uid = (String) jwtAuthenticationToken.getTokenAttributes().get("uid");
        Optional<AppUser> appUserOptional = appUserRepository.findByUid(uid);

        LocalDateTime now = LocalDateTime.now();
        // By default, the name maps to the sub claim according to Spring Security docs
        String email = jwtAuthenticationToken.getName();
        if (appUserOptional.isEmpty()) {
            AppUser newUser = AppUser.builder()
                    .uid(uid)
                    .email(email)
                    .registrationDateTime(now)
                    .lastAccessDateTime(now)
                    .build();
            appUserRepository.save(newUser);
            jwtAuthenticationToken.setDetails(newUser);
//            emailService.send(newUser.getEmail(), "Welcome!", "Welcome to Daily :)");
        } else {
            AppUser appUser = appUserOptional.get();
            if (!appUser.getEmail().equals(email)) {
                // User changed email address, update accordingly
                appUser.setEmail(email);
            }
            appUser.setLastAccessDateTime(now);
            appUserRepository.save(appUser);
            jwtAuthenticationToken.setDetails(appUser);
        }
    }

    @Override
    public AppUser getAppUserFromToken() throws ResponseStatusException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (jwtAuthenticationToken == null || jwtAuthenticationToken.getDetails() == null || !(jwtAuthenticationToken.getDetails() instanceof AppUser)) {
            // As per the current system design, this situation should not happen
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }

        return (AppUser) jwtAuthenticationToken.getDetails();
    }

}
