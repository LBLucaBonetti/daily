package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;

import javax.persistence.EntityNotFoundException;

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
     * @return Read tag
     * @throws IllegalStateException   If the user does not own the tag
     * @throws EntityNotFoundException If the tag does not exist
     */
    Tag readTag(Long id, AppUser appUser) throws IllegalStateException, EntityNotFoundException;

    /**
     * Updates a tag
     *
     * @param id      Tag id
     * @param tag     Updated tag
     * @param appUser User
     * @throws IllegalStateException   If the user does not own the tag
     * @throws EntityNotFoundException If the tag does not exist
     */
    void updateTag(Long id, Tag tag, AppUser appUser) throws IllegalStateException, EntityNotFoundException;

    /**
     * Deletes a tag
     *
     * @param id      Tag id
     * @param appUser User
     * @throws IllegalStateException   If the user does not own the tag
     * @throws EntityNotFoundException If the tag does not exist
     */
    void deleteTag(Long id, AppUser appUser) throws IllegalStateException, EntityNotFoundException;

}
