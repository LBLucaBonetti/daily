package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.tags.Tag;
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
   * @param appUser Unique id of the appUser
   * @return Created note
   */
  NoteDto createNote(NoteDto note, String appUser);

  /**
   * Reads a note
   *
   * @param uuid Note uuid
   * @param appUser Unique id of the appUser
   * @return Read note or empty value
   */
  Optional<NoteDto> readNote(UUID uuid, String appUser);

  /**
   * Reads notes
   *
   * @param pageable Pagination and sorting object
   * @param appUser Unique id of the appUser
   * @return Read notes or empty page
   */
  Page<Note> readNotes(Pageable pageable, String appUser);

  /**
   * Updates a note
   *
   * @param uuid Note uuid
   * @param note Note object with new data
   * @param appUser Unique id of the appUser
   * @return Updated note or empty value
   */
  Optional<Note> updateNote(UUID uuid, Note note, String appUser);

  /**
   * Deletes a note
   *
   * @param uuid Note uuid
   * @param appUser Unique id of the appUser
   * @return True if the note is deleted, false otherwise
   */
  Boolean deleteNote(UUID uuid, String appUser);

  /**
   * Adds a tag to a note
   *
   * @param uuid Note uuid
   * @param tagUuid Tag uuid
   * @param appUser Unique id of the appUser
   * @return True if the tag is correctly added to the note, false otherwise
   */
  Boolean addTagToNote(UUID uuid, UUID tagUuid, String appUser);

  /**
   * Removes a tag from a note
   *
   * @param uuid Note uuid
   * @param tagUuid Tag uuid
   * @param appUser Unique id of the appUser
   * @return True if the tag is correctly removed from the note, false otherwise
   */
  Boolean removeTagFromNote(UUID uuid, UUID tagUuid, String appUser);

  /**
   * Reads note tags
   *
   * @param uuid Note uuid
   * @param appUser Unique id of the appUser
   * @return Read note tags or empty value if the note does not exist
   */
  Optional<Set<Tag>> readNoteTags(UUID uuid, String appUser);
}
