package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * Finds a tag by its id and AppUser
     *
     * @param id      Tag id
     * @param appUser User
     * @return Found tag or empty value
     */
    Optional<Tag> findByIdAndAppUser(Long id, AppUser appUser);

}
