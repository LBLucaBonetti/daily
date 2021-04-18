package it.lbsoftware.daily.appusers;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface AppUserService {

    /**
     * Creates a new user if it does not exist, updates its information otherwise
     *
     * @param jwtAuthenticationToken Token that contains user details
     */
    void checkAppUserRegistration(JwtAuthenticationToken jwtAuthenticationToken);

}
