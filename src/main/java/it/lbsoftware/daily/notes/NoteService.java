package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;

import java.util.List;
import java.util.Optional;
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

}
