package it.lbsoftware.daily.tags;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {

  /**
   * Finds a tag by its uuid and AppUser
   *
   * @param uuid Tag uuid
   * @param appUser Unique id of the appUser
   * @return Found tag or empty value
   */
  Optional<Tag> findByUuidAndAppUser(UUID uuid, String appUser);

  /**
   * Finds tags by their AppUser
   *
   * @param appUser Unique id of the appUser
   * @return Found tags or empty list
   */
  List<Tag> findByAppUser(String appUser);

  /**
   * Finds a tag by its uuid and AppUser fetching the associated notes
   *
   * @param uuid Tag uuid
   * @param appUser Unique id of the appUser
   * @return Found tag or empty value
   */
  @Query(
      "select t from Tag t left join fetch t.noteSet where t.uuid = :uuid and t.appUser = :appUser")
  Optional<Tag> findByUuidAndAppUserFetchNotes(
      @Param("uuid") UUID uuid, @Param("appUser") String appUser);
}
