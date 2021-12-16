package it.lbsoftware.daily.appusers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class AppUserServiceImplTests {

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private AppUserServiceImpl appUserService;

    private final String appUserUid = "123";
    private final String appUserEmail = "appUser@daily.email";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void givenJwtAuthenticationTokenWithNewAppUserData_whenSetAppUserToToken_thenSaveNewAppUser() {
        // given
        AppUser appUser = AppUser.builder().uid(appUserUid).email(appUserEmail).build();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", appUserEmail)
                .claim("uid", appUserUid)
                .build();
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("SCOPE_read", "SCOPE_write");
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);
        // when
        appUserService.setAppUserToToken(token);
        // then
        assertTrue(token.getDetails() instanceof AppUser);
        AppUser detailsAppUser = (AppUser) token.getDetails();
        assertEquals(appUser.getEmail(), detailsAppUser.getEmail());
        assertEquals(appUser.getUid(), detailsAppUser.getUid());
        assertTrue(appUserRepository.count() == 1);
    }

    @Test
    void givenJwtAuthenticationTokenWithNewEmailForExistingAppUser_whenSetAppUserToToken_thenUpdateEmailForExistingAppUser() {
        // given
        AppUser appUser = AppUser.builder().uid(appUserUid).email(appUserEmail).build();
        appUserRepository.save(appUser);
        final String newAppUserEmail = "newEmailForAppUser@daily.email";
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", newAppUserEmail)
                .claim("uid", appUserUid)
                .build();
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("SCOPE_read", "SCOPE_write");
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);
        // when
        appUserService.setAppUserToToken(token);
        // then
        assertTrue(token.getDetails() instanceof AppUser);
        AppUser detailsAppUser = (AppUser) token.getDetails();
        assertEquals(newAppUserEmail, detailsAppUser.getEmail());
        assertEquals(appUserUid, detailsAppUser.getUid());
        assertTrue(appUserRepository.count() == 1);
    }

    @Test
    @WithMockAppUser
    void givenNoAppUser_whenGetAppUserFromToken_thenReturnEmpty() {
        Optional<AppUser> optionalAppUser = appUserService.getAppUserFromToken();
        assertTrue(optionalAppUser.isEmpty());
    }

    @Test
    @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
    void givenAppUser_whenGetAppUserFromToken_thenReturnAppUser() {
        Optional<AppUser> optionalAppUser = appUserService.getAppUserFromToken();
        assertTrue(optionalAppUser.isPresent());
        AppUser appUser = optionalAppUser.get();
        assertEquals(appUserUid, appUser.getUid());
        assertEquals(appUserEmail, appUser.getEmail());
    }

}