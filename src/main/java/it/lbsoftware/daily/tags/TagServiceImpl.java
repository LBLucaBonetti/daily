package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.notes.Note;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;

  @Override
  public Tag createTag(@NonNull Tag tag, @NonNull AppUser appUser) {
    tag.setAppUser(appUser);

    return tagRepository.save(tag);
  }

  @Override
  public Optional<Tag> readTag(@NonNull UUID uuid, @NonNull AppUser appUser) {
    return tagRepository.findByUuidAndAppUser(uuid, appUser);
  }

  @Override
  public List<Tag> readTags(@NonNull AppUser appUser) {
    return tagRepository.findByAppUser(appUser);
  }

  @Override
  @Transactional
  public Optional<Tag> updateTag(@NonNull UUID uuid, @NonNull Tag tag, @NonNull AppUser appUser) {
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
  @Transactional
  public Boolean deleteTag(@NonNull UUID uuid, @NonNull AppUser appUser) {
    Optional<Tag> tagOptional = tagRepository.findByUuidAndAppUser(uuid, appUser);
    if (tagOptional.isEmpty()) {
      return false;
    }
    Tag tag = tagOptional.get();
    for (Note note : tag.getNoteSet()) {
      note.getTagSet().remove(tag);
    }
    tagRepository.delete(tag);

    return true;
  }
}
