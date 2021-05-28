package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public Optional<Tag> readTag(Long id, AppUser appUser) {
        return tagRepository.findByIdAndAppUser(id, appUser);
    }

    @Override
    public Optional<Tag> updateTag(Long id, Tag tag, AppUser appUser) {
        Optional<Tag> tagOptional = tagRepository.findByIdAndAppUser(id, appUser);
        if (tagOptional.isEmpty()) {
            return Optional.empty();
        }
        Tag prevTag = tagOptional.get();
        prevTag.setName(tag.getName());
        prevTag.setColorHex(tag.getColorHex());

        return Optional.of(tagRepository.save(prevTag));
    }

    @Override
    public Boolean deleteTag(Long id, AppUser appUser) {
        Optional<Tag> tagOptional = tagRepository.findByIdAndAppUser(id, appUser);
        if (tagOptional.isEmpty()) {
            return false;
        }
        tagRepository.delete(tagOptional.get());

        return true;
    }

}
