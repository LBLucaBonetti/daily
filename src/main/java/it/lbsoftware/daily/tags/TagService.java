package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;

import java.util.Optional;

public interface TagService {

    /**
     * Creates a tag
     *
     * @param tag     Tag object to be created
     * @param appUser User
     * @return Created tag
     */
    Tag createTag(Tag tag, AppUser appUser);

    /**
     * Reads a tag
     *
     * @param id      Tag id
     * @param appUser User
     * @return Read tag or empty value
     */
    Optional<Tag> readTag(Long id, AppUser appUser);

    /**
     * Updates a tag
     *
     * @param id      Tag id
     * @param tag     Tag object with new data
     * @param appUser User
     * @return Updated tag or empty value
     */
    Optional<Tag> updateTag(Long id, Tag tag, AppUser appUser);

    /**
     * Deletes a tag
     *
     * @param id      Tag id
     * @param appUser User
     * @return True if the tag is deleted, false otherwise
     */
    Boolean deleteTag(Long id, AppUser appUser);

}
