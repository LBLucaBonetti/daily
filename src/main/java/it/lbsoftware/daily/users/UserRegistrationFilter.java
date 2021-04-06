package it.lbsoftware.daily.users;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserRegistrationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        /*
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        System.out.println("--- USER FILTER ---");
        System.out.println("Sub (email): " + jwtAuthenticationToken.getName());
        System.out.println("Uid (user id): " + jwtAuthenticationToken.getTokenAttributes().get("uid"));
         */

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
