package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoteRepository extends JpaRepository<Note, Long> {

  /**
   * Finds a note by its uuid and AppUser
   *
   * @param uuid Note uuid
   * @param appUser The appUser
   * @return Found note or empty value
   */
  Optional<Note> findByUuidAndAppUser(UUID uuid, AppUser appUser);

  /**
   * Finds notes by their AppUser
   *
   * @param pageable Pagination and sorting object
   * @param appUser The appUser
   * @return Found notes or empty page
   */
  Page<Note> findByAppUser(Pageable pageable, AppUser appUser);

  /**
   * Finds a note by its uuid and AppUser fetching the associated tags
   *
   * @param uuid Note uuid
   * @param appUser Unique id of the appUser
   * @return Found note or empty value
   */
  @Query(
      "select n from Note n left join fetch n.tags where n.uuid = :uuid and n.appUser = :appUser")
  Optional<Note> findByUuidAndAppUserFetchTags(
      @Param("uuid") UUID uuid, @Param("appUser") AppUser appUser);

  @Query("delete from Note n where n.appUser = :appUser")
  @Modifying
  void deleteByAppUser(@Param("appUser") AppUser appUser);
}
