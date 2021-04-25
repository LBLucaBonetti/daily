package it.lbsoftware.daily.appusers;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import javax.persistence.EntityNotFoundException;

public interface AppUserService {

    /**
     * Creates a new user if it does not exist, updates its information otherwise
     *
     * @param jwtAuthenticationToken Token that contains user details
     */
    void checkAppUserRegistration(JwtAuthenticationToken jwtAuthenticationToken);

    /**
     * Gets the user from the provided token
     *
     * @param jwtAuthenticationToken Token that contains user details
     * @return Found user
     * @throws EntityNotFoundException If the user does not exist
     */
    AppUser getAppUserFromToken(JwtAuthenticationToken jwtAuthenticationToken) throws EntityNotFoundException;

}
