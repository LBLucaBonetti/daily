package it.lbsoftware.daily.appusers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;

    @Override
    public void setAppUserToToken(JwtAuthenticationToken jwtAuthenticationToken) {
        String uid = (String) jwtAuthenticationToken.getTokenAttributes().get("uid");
        Optional<AppUser> appUserOptional = appUserRepository.findByUid(uid);

        // By default, the name maps to the sub claim according to Spring Security docs
        String email = jwtAuthenticationToken.getName();
        if (appUserOptional.isEmpty()) {
            AppUser newUser = AppUser.builder()
                    .uid(uid)
                    .email(email)
                    .build();
            appUserRepository.save(newUser);
            jwtAuthenticationToken.setDetails(newUser);
        } else {
            AppUser appUser = appUserOptional.get();
            if (!appUser.getEmail().equals(email)) {
                // User changed email address, update accordingly
                appUser.setEmail(email);
            }
            appUserRepository.save(appUser);
            jwtAuthenticationToken.setDetails(appUser);
        }
    }

    @Override
    public Optional<AppUser> getAppUserFromToken() {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (jwtAuthenticationToken == null || jwtAuthenticationToken.getDetails() == null || !(jwtAuthenticationToken.getDetails() instanceof AppUser)) {
            // As per the current system design, this situation should not happen
            return Optional.empty();
        }

        return Optional.of((AppUser) jwtAuthenticationToken.getDetails());
    }

}