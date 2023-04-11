package it.lbsoftware.daily.notes;

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
   * @param appUser Unique id of the appUser
   * @return Created note
   */
  NoteDto createNote(NoteDto note, UUID appUser);

  /**
   * Reads a note
   *
   * @param uuid Note uuid
   * @param appUser Unique id of the appUser
   * @return Read note or empty value
   */
  Optional<NoteDto> readNote(UUID uuid, UUID appUser);

  /**
   * Reads notes
   *
   * @param pageable Pagination and sorting object
   * @param appUser Unique id of the appUser
   * @return Read notes or empty page
   */
  Page<NoteDto> readNotes(Pageable pageable, UUID appUser);

  /**
   * Updates a note
   *
   * @param uuid Note uuid
   * @param note Note object with new data
   * @param appUser Unique id of the appUser
   * @return Updated note or empty value
   */
  Optional<NoteDto> updateNote(UUID uuid, NoteDto note, UUID appUser);

  /**
   * Deletes a note
   *
   * @param uuid Note uuid
   * @param appUser Unique id of the appUser
   */
  void deleteNote(UUID uuid, UUID appUser);

  /**
   * Adds a tag to a note
   *
   * @param uuid Note uuid
   * @param tagUuid Tag uuid
   * @param appUser Unique id of the appUser
   */
  void addTagToNote(UUID uuid, UUID tagUuid, UUID appUser);

  /**
   * Removes a tag from a note
   *
   * @param uuid Note uuid
   * @param tagUuid Tag uuid
   * @param appUser Unique id of the appUser
   */
  void removeTagFromNote(UUID uuid, UUID tagUuid, UUID appUser);

  /**
   * Reads note tags
   *
   * @param uuid Note uuid
   * @param appUser Unique id of the appUser
   * @return Read note tags or empty value if the note does not exist
   */
  Optional<Set<TagDto>> readNoteTags(UUID uuid, UUID appUser);
}
