package it.lbsoftware.daily.appusers;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import javax.validation.constraints.NotNull;

public interface AppUserService {

    /**
     * Creates a new user if it does not exist, updates its last access otherwise
     *
     * @param jwtAuthenticationToken Token that contains user details
     */
    void checkAppUserPresence(@NotNull JwtAuthenticationToken jwtAuthenticationToken);

}
