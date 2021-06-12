package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public Optional<Tag> readTag(UUID uuid, AppUser appUser) {
        return tagRepository.findByUuidAndAppUser(uuid, appUser);
    }

    @Override
    public List<Tag> readTags(AppUser appUser) {
        return tagRepository.findByAppUser(appUser);
    }

    @Override
    public Optional<Tag> updateTag(UUID uuid, Tag tag, AppUser appUser) {
        Optional<Tag> tagOptional = tagRepository.findByUuidAndAppUser(uuid, appUser);
        if (tagOptional.isEmpty()) {
            return Optional.empty();
        }
        Tag prevTag = tagOptional.get();
        prevTag.setName(tag.getName());
        prevTag.setColorHex(tag.getColorHex());

        return Optional.of(tagRepository.save(prevTag));
    }

    @Override
    public Boolean deleteTag(UUID uuid, AppUser appUser) {
        Optional<Tag> tagOptional = tagRepository.findByUuidAndAppUser(uuid, appUser);
        if (tagOptional.isEmpty()) {
            return false;
        }
        tagRepository.delete(tagOptional.get());

        return true;
    }

}
