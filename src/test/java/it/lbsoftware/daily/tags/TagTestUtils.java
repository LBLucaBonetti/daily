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
   * @param uuid
   * @param name
   * @param colorHex
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
   * @param name
   * @param colorHex
   * @param noteSet
   * @param appUser
   * @return The created Tag
   */
  public static Tag createTag(
      final String name, final String colorHex, final Set<Note> noteSet, final String appUser) {
    return Tag.builder().name(name).colorHex(colorHex).noteSet(noteSet).appUser(appUser).build();
  }
}
