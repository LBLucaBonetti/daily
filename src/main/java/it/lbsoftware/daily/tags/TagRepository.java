package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Main {@link Tag} repository. */
public interface TagRepository extends JpaRepository<Tag, Long> {

  /**
   * Finds a tag by its uuid and AppUser.
   *
   * @param uuid Tag uuid
   * @param appUser The appUser
   * @return Found tag or empty value
   */
  Optional<Tag> findByUuidAndAppUser(UUID uuid, AppUser appUser);

  /**
   * Finds tags by their AppUser.
   *
   * @param pageable Pagination and sorting object
   * @param appUser The appUser
   * @return Found tags or empty page
   */
  Page<Tag> findByAppUser(Pageable pageable, AppUser appUser);

  /**
   * Finds a tag by its uuid and AppUser fetching the associated notes.
   *
   * @param uuid Tag uuid
   * @param appUser The appUser
   * @return Found tag or empty value
   */
  @Query(
      "select t from Tag t left join fetch t.notes where t.uuid = :uuid and t.appUser = :appUser")
  Optional<Tag> findByUuidAndAppUserFetchNotes(
      @Param("uuid") UUID uuid, @Param("appUser") AppUser appUser);

  @Query("delete from Tag t where t.appUser = :appUser")
  @Modifying
  void deleteByAppUser(@Param("appUser") AppUser appUser);
}
