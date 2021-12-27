package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Finds notes containing a certain string
     *
     * @param contained The string to be found
     * @return Found notes or empty list
     */
    List<Note> findByTextContaining(String contained);

    /**
     * Finds a note by its uuid and AppUser
     *
     * @param uuid    Note uuid
     * @param appUser User
     * @return Found note or empty value
     */
    Optional<Note> findByUuidAndAppUser(UUID uuid, AppUser appUser);

    /**
     * Finds notes by their AppUser
     *
     * @param appUser User
     * @return Found notes or empty list
     */
    List<Note> findByAppUser(AppUser appUser);

    /**
     * Finds a note by its uuid and AppUser fetching the associated tags
     *
     * @param uuid    Note uuid
     * @param appUser User
     * @return Found note or empty value
     */
    @Query("select n from Note n left join fetch n.tagSet where n.uuid = :uuid and n.appUser = :appUser")
    Optional<Note> findByUuidAndAppUserFetchTags(@Param("uuid") UUID uuid, @Param("appUser") AppUser appUser);

}