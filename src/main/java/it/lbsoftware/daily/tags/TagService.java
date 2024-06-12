package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.search.SearchCriteriaRetriever;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service to deal with {@link Tag} entities. */
public interface TagService {

  /**
   * Creates a tag.
   *
   * @param tag Tag object to be created
   * @param appUser The owner
   * @return Created tag
   */
  TagDto createTag(TagDto tag, AppUser appUser);

  /**
   * Reads a tag.
   *
   * @param uuid Tag uuid
   * @param appUser The owner
   * @return Read tag or empty value
   */
  Optional<TagDto> readTag(UUID uuid, AppUser appUser);

  /**
   * Reads tags with pagination and no search filters.
   *
   * @param pageable Pagination and sorting object
   * @param appUser The owner
   * @return Read tags or empty list
   */
  Page<TagDto> readTags(Pageable pageable, AppUser appUser);

  /**
   * Updates a tag.
   *
   * @param uuid Tag uuid
   * @param tag Tag object with new data
   * @param appUser The owner
   * @return Updated tag or empty value
   */
  Optional<TagDto> updateTag(UUID uuid, TagDto tag, AppUser appUser);

  /**
   * Deletes a tag.
   *
   * @param uuid Tag uuid
   * @param appUser The owner
   */
  void deleteTag(UUID uuid, AppUser appUser);

  /**
   * Reads tags with pagination and search filters.
   *
   * @param search A search criteria retriever for this entity
   * @param pageable Pagination and sorting object
   * @param appUser The owner
   * @return Read tags or empty list
   */
  Page<TagDto> searchTags(SearchCriteriaRetriever<Tag> search, Pageable pageable, AppUser appUser);
}
