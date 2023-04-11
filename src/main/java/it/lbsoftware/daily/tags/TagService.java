package it.lbsoftware.daily.tags;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {

  /**
   * Creates a tag
   *
   * @param tag Tag object to be created
   * @param appUser Unique id of the appUser
   * @return Created tag
   */
  TagDto createTag(TagDto tag, UUID appUser);

  /**
   * Reads a tag
   *
   * @param uuid Tag uuid
   * @param appUser Unique id of the appUser
   * @return Read tag or empty value
   */
  Optional<TagDto> readTag(UUID uuid, UUID appUser);

  /**
   * Reads tags
   *
   * @param pageable Pagination and sorting object
   * @param appUser Unique id of the appUser
   * @return Read tags or empty list
   */
  Page<TagDto> readTags(Pageable pageable, UUID appUser);

  /**
   * Updates a tag
   *
   * @param uuid Tag uuid
   * @param tag Tag object with new data
   * @param appUser Unique id of the appUser
   * @return Updated tag or empty value
   */
  Optional<TagDto> updateTag(UUID uuid, TagDto tag, UUID appUser);

  /**
   * Deletes a tag
   *
   * @param uuid Tag uuid
   * @param appUser Unique id of the appUser
   */
  void deleteTag(UUID uuid, UUID appUser);
}
