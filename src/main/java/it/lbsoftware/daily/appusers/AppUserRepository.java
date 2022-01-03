package it.lbsoftware.daily.appusers;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  /**
   * Finds user by its uid
   *
   * @param uid The uid of the user
   * @return Found user or empty value
   */
  Optional<AppUser> findByUid(String uid);

  /**
   * Finds user by its email
   *
   * @param email The email of the user
   * @return Found user or empty value
   */
  Optional<AppUser> findByEmail(String email);
}
