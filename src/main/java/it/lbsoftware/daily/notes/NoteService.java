package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.tags.TagDto;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoteService {

  /**
   * Creates a note
   *
   * @param note Note object to be created
   * @param appUser The owner
   * @return Created note
   */
  NoteDto createNote(NoteDto note, AppUser appUser);

  /**
   * Reads a note
   *
   * @param uuid Note uuid
   * @param appUser The owner
   * @return Read note or empty value
   */
  Optional<NoteDto> readNote(UUID uuid, AppUser appUser);

  /**
   * Reads notes
   *
   * @param pageable Pagination and sorting object
   * @param appUser The owner
   * @return Read notes or empty page
   */
  Page<NoteDto> readNotes(Pageable pageable, AppUser appUser);

  /**
   * Updates a note
   *
   * @param uuid Note uuid
   * @param note Note object with new data
   * @param appUser The owner
   * @return Updated note or empty value
   */
  Optional<NoteDto> updateNote(UUID uuid, NoteDto note, AppUser appUser);

  /**
   * Deletes a note
   *
   * @param uuid Note uuid
   * @param appUser The owner
   */
  void deleteNote(UUID uuid, AppUser appUser);

  /**
   * Adds a tag to a note
   *
   * @param uuid Note uuid
   * @param tagUuid Tag uuid
   * @param appUser The owner
   */
  void addTagToNote(UUID uuid, UUID tagUuid, AppUser appUser);

  /**
   * Removes a tag from a note
   *
   * @param uuid Note uuid
   * @param tagUuid Tag uuid
   * @param appUser The owner
   */
  void removeTagFromNote(UUID uuid, UUID tagUuid, AppUser appUser);

  /**
   * Reads note tags
   *
   * @param uuid Note uuid
   * @param appUser The owner
   * @return Read note tags or empty value if the note does not exist
   */
  Optional<Set<TagDto>> readNoteTags(UUID uuid, AppUser appUser);
}
