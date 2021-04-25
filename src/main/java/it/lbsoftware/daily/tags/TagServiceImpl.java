package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public Tag createTag(Tag tag, AppUser appUser) {
        tag.setAppUser(appUser);
        return tagRepository.save(tag);
    }

    @Override
    public Tag readTag(Long id, AppUser appUser) {
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (tagOptional.isPresent()) {
            Tag prevTag = tagOptional.get();
            if (!prevTag.getAppUser().equals(appUser)) {
                throw new IllegalStateException();
            }
            return prevTag;
        }

        throw new EntityNotFoundException();
    }

    @Override
    public void updateTag(Long id, Tag tag, AppUser appUser) throws IllegalStateException, EntityNotFoundException {
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (tagOptional.isPresent()) {
            Tag prevTag = tagOptional.get();
            if (!prevTag.getAppUser().equals(appUser)) {
                throw new IllegalStateException();
            }
            prevTag.setName(tag.getName());
            prevTag.setColorHex(tag.getColorHex());
            tagRepository.save(prevTag);
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void deleteTag(Long id, AppUser appUser) throws IllegalStateException, EntityNotFoundException {
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (tagOptional.isPresent()) {
            Tag prevTag = tagOptional.get();
            if (!prevTag.getAppUser().equals(appUser)) {
                throw new IllegalStateException();
            }
            tagRepository.delete(prevTag);
        } else {
            throw new EntityNotFoundException();
        }
    }

}
