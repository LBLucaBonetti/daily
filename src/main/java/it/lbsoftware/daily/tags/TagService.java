package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;

import java.util.Optional;
import java.util.UUID;

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
     * @param uuid    Tag uuid
     * @param appUser User
     * @return Read tag or empty value
     */
    Optional<Tag> readTag(UUID uuid, AppUser appUser);

    /**
     * Updates a tag
     *
     * @param uuid    Tag uuid
     * @param tag     Tag object with new data
     * @param appUser User
     * @return Updated tag or empty value
     */
    Optional<Tag> updateTag(UUID uuid, Tag tag, AppUser appUser);

    /**
     * Deletes a tag
     *
     * @param uuid    Tag uuid
     * @param appUser User
     * @return True if the tag is deleted, false otherwise
     */
    Boolean deleteTag(UUID uuid, AppUser appUser);

}
