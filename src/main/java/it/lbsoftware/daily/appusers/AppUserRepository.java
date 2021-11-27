package it.lbsoftware.daily.appusers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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