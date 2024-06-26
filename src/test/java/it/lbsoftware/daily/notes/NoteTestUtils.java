package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.tags.Tag;
import java.util.Set;
import java.util.UUID;

public final class NoteTestUtils {

  private NoteTestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * {@link NoteDto} generator.
   *
   * @param uuid Unique identifier
   * @param text Text content
   * @return The created {@link NoteDto}
   */
  public static NoteDto createNoteDto(final UUID uuid, final String text) {
    NoteDto noteDto = new NoteDto();
    noteDto.setUuid(uuid);
    noteDto.setText(text);
    return noteDto;
  }

  /**
   * {@link Note} generator; the created instance is not persisted.
   *
   * @param text Text content
   * @param tags Tags set
   * @param appUser The creator
   * @return The created {@link Note}
   */
  public static Note createNote(final String text, final Set<Tag> tags, final AppUser appUser) {
    return Note.builder().text(text).tags(tags).appUser(appUser).build();
  }
}
