package it.lbsoftware.daily.appusers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * Annotation to be used in order to obtain a valid authenticated JwtAuthenticationToken from
 * SecurityContextHolder.getContext().getAuthentication(). If the uid or email parameters are left
 * blank, then no valid authentication will be provided. The setAppUserAsDetails parameter can be
 * set to true to have a new AppUser with the provided data as the details of the
 * JwtAuthenticationToken, only if the data is not left blank
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAppUserSecurityContextFactory.class)
public @interface WithMockAppUser {

  String uid() default "";

  String email() default "";

  boolean setAppUserAsDetails() default false;
}
