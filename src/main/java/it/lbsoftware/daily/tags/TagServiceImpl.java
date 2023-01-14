package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.notes.Note;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;

  @Override
  public Tag createTag(@NonNull Tag tag, @NonNull String appUser) {
    tag.setAppUser(appUser);

    return tagRepository.save(tag);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Tag> readTag(@NonNull UUID uuid, @NonNull String appUser) {
    return tagRepository.findByUuidAndAppUser(uuid, appUser);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Tag> readTags(Pageable pageable, @NonNull String appUser) {
    return tagRepository.findByAppUser(pageable, appUser);
  }

  @Override
  @Transactional
  public Optional<Tag> updateTag(@NonNull UUID uuid, @NonNull Tag tag, @NonNull String appUser) {
    return tagRepository
        .findByUuidAndAppUser(uuid, appUser)
        .map(
            prevTag -> {
              prevTag.setName(tag.getName());
              prevTag.setColorHex(tag.getColorHex());
              return tagRepository.save(prevTag);
            });
  }

  @Override
  @Transactional
  public Boolean deleteTag(@NonNull UUID uuid, @NonNull String appUser) {
    Optional<Tag> tagOptional = tagRepository.findByUuidAndAppUser(uuid, appUser);
    if (tagOptional.isEmpty()) {
      return false;
    }
    Tag tag = tagOptional.get();
    for (Note note : tag.getNotes()) {
      note.getTags().remove(tag);
    }
    tagRepository.delete(tag);

    return true;
  }
}
