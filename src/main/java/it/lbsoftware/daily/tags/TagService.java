package it.lbsoftware.daily.tags;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagService {

  /**
   * Creates a tag
   *
   * @param tag Tag object to be created
   * @param appUser Unique id of the appUser
   * @return Created tag
   */
  Tag createTag(Tag tag, String appUser);

  /**
   * Reads a tag
   *
   * @param uuid Tag uuid
   * @param appUser Unique id of the appUser
   * @return Read tag or empty value
   */
  Optional<Tag> readTag(UUID uuid, String appUser);

  /**
   * Reads tags
   *
   * @param appUser Unique id of the appUser
   * @return Read tags or empty list
   */
  List<Tag> readTags(String appUser);

  /**
   * Updates a tag
   *
   * @param uuid Tag uuid
   * @param tag Tag object with new data
   * @param appUser Unique id of the appUser
   * @return Updated tag or empty value
   */
  Optional<Tag> updateTag(UUID uuid, Tag tag, String appUser);

  /**
   * Deletes a tag
   *
   * @param uuid Tag uuid
   * @param appUser Unique id of the appUser
   * @return True if the tag is deleted, false otherwise
   */
  Boolean deleteTag(UUID uuid, String appUser);
}
