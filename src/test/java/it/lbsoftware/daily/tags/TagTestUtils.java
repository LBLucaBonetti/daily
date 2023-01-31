package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.notes.Note;
import java.util.Set;
import java.util.UUID;

public final class TagTestUtils {

  private TagTestUtils() {
    throw new UnsupportedOperationException("This class cannot be instantiated!");
  }

  /**
   * TagDto generator
   *
   * @param uuid Unique identifier
   * @param name Name content
   * @param colorHex Color hex content
   * @return The created TagDto
   */
  public static TagDto createTagDto(final UUID uuid, final String name, final String colorHex) {
    TagDto tagDto = new TagDto();
    tagDto.setUuid(uuid);
    tagDto.setName(name);
    tagDto.setColorHex(colorHex);
    return tagDto;
  }

  /**
   * Tag generator; the created instance is not persisted
   *
   * @param name Name content
   * @param colorHex Color hex content
   * @param notes Notes set
   * @param appUser The creator
   * @return The created Tag
   */
  public static Tag createTag(
      final String name, final String colorHex, final Set<Note> notes, final String appUser) {
    return Tag.builder().name(name).colorHex(colorHex).notes(notes).appUser(appUser).build();
  }
}
