package it.lbsoftware.daily.appusers;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public interface AppUserService {

    /**
     * Creates a new user if it does not exist, updates its information otherwise; details are stored as Authentication details
     *
     * @param jwtAuthenticationToken Token that provides user details and to set user details to
     */
    void setAppUserToToken(JwtAuthenticationToken jwtAuthenticationToken);

    /**
     * Gets the request user from the token
     *
     * @return Found user or empty value
     */
    Optional<AppUser> getAppUserFromToken();

}