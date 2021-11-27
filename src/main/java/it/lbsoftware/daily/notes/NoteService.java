package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.tags.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface NoteService {

    /**
     * Creates a note
     *
     * @param note    Note object to be created
     * @param appUser User
     * @return Created note
     */
    Note createNote(Note note, AppUser appUser);

    /**
     * Reads a note
     *
     * @param uuid    Note uuid
     * @param appUser User
     * @return Read note or empty value
     */
    Optional<Note> readNote(UUID uuid, AppUser appUser);

    /**
     * Reads notes
     *
     * @param appUser User
     * @return Read notes or empty list
     */
    List<Note> readNotes(AppUser appUser);

    /**
     * Updates a note
     *
     * @param uuid    Note uuid
     * @param note    Note object with new data
     * @param appUser User
     * @return Updated note or empty value
     */
    Optional<Note> updateNote(UUID uuid, Note note, AppUser appUser);

    /**
     * Deletes a note
     *
     * @param uuid    Note uuid
     * @param appUser User
     * @return True if the note is deleted, false otherwise
     */
    Boolean deleteNote(UUID uuid, AppUser appUser);

    /**
     * Adds a tag to a note
     *
     * @param uuid    Note uuid
     * @param tagUuid Tag uuid
     * @param appUser User
     * @return True if the tag is correctly added to the note, false otherwise
     */
    Boolean addTagToNote(UUID uuid, UUID tagUuid, AppUser appUser);

    /**
     * Removes a tag from a note
     *
     * @param uuid    Note uuid
     * @param tagUuid Tag uuid
     * @param appUser User
     * @return True if the tag is correctly removed from the note, false otherwise
     */
    Boolean removeTagFromNote(UUID uuid, UUID tagUuid, AppUser appUser);

    /**
     * Reads note tags
     *
     * @param uuid    Note uuid
     * @param appUser User
     * @return Read note tags or empty value if the note does not exist
     */
    Optional<Set<Tag>> readNoteTags(UUID uuid, AppUser appUser);

}