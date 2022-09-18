package it.lbsoftware.daily.notes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoteRepository extends JpaRepository<Note, Long> {

  /**
   * Finds a note by its uuid and AppUser
   *
   * @param uuid Note uuid
   * @param appUser Unique id of the appUser
   * @return Found note or empty value
   */
  Optional<Note> findByUuidAndAppUser(UUID uuid, String appUser);

  /**
   * Finds notes by their AppUser
   *
   * @param appUser Unique id of the appUser
   * @return Found notes or empty list
   */
  List<Note> findByAppUser(String appUser);

  /**
   * Finds a note by its uuid and AppUser fetching the associated tags
   *
   * @param uuid Note uuid
   * @param appUser Unique id of the appUser
   * @return Found note or empty value
   */
  @Query(
      "select n from Note n left join fetch n.tagSet where n.uuid = :uuid and n.appUser = :appUser")
  Optional<Note> findByUuidAndAppUserFetchTags(
      @Param("uuid") UUID uuid, @Param("appUser") String appUser);
}
