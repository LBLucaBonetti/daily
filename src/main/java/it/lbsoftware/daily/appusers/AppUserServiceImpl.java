package it.lbsoftware.daily.appusers;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService{

  private static final String UID_CLAIM = "sub";
  private static final String UID_INVALID = "The OidcUser did not provide a valid unique id";

  @Override
  public String getUid(OidcUser appUser) {
    return Optional.ofNullable(appUser)
        .map(OidcUser::getIdToken)
        .map(OidcIdToken::getClaims)
        .map(claims -> (String) claims.get(UID_CLAIM))
        .filter(StringUtils::isNotBlank)
        .orElseThrow(() -> new IllegalArgumentException(UID_INVALID));
  }
}
