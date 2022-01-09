package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

  /**
   * Finds a tag by its uuid and AppUser
   *
   * @param uuid Tag uuid
   * @param appUser User
   * @return Found tag or empty value
   */
  Optional<Tag> findByUuidAndAppUser(UUID uuid, AppUser appUser);

  /**
   * Finds tags by their AppUser
   *
   * @param appUser User
   * @return Found tags or empty list
   */
  List<Tag> findByAppUser(AppUser appUser);
}
