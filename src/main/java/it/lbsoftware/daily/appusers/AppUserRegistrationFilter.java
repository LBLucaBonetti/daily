package it.lbsoftware.daily.appusers;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AppUserRegistrationFilter extends OncePerRequestFilter {

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
