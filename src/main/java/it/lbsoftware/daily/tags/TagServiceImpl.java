package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exception.DailyNotFoundException;
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
  private final TagDtoMapper tagDtoMapper;

  @Override
  public TagDto createTag(@NonNull TagDto tag, @NonNull String appUser) {
    Tag tagEntity = tagDtoMapper.convertToEntity(tag);
    tagEntity.setAppUser(appUser);
    Tag savedTagEntity = tagRepository.save(tagEntity);

    return tagDtoMapper.convertToDto(savedTagEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<TagDto> readTag(@NonNull UUID uuid, @NonNull String appUser) {
    return tagRepository.findByUuidAndAppUser(uuid, appUser).map(tagDtoMapper::convertToDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TagDto> readTags(Pageable pageable, @NonNull String appUser) {
    return tagRepository.findByAppUser(pageable, appUser).map(tagDtoMapper::convertToDto);
  }

  @Override
  @Transactional
  public Optional<TagDto> updateTag(
      @NonNull UUID uuid, @NonNull TagDto tag, @NonNull String appUser) {
    return tagRepository
        .findByUuidAndAppUser(uuid, appUser)
        .map(
            prevTag -> {
              prevTag.setName(tag.getName());
              prevTag.setColorHex(tag.getColorHex());
              return tagRepository.save(prevTag);
            })
        .map(tagDtoMapper::convertToDto);
  }

  @Override
  @Transactional
  public void deleteTag(@NonNull UUID uuid, @NonNull String appUser) {
    Tag tag =
        tagRepository
            .findByUuidAndAppUser(uuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    for (Note note : tag.getNotes()) {
      note.getTags().remove(tag);
    }
    tagRepository.delete(tag);
  }
}
