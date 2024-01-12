package it.lbsoftware.daily.tags;

import static it.lbsoftware.daily.config.Constants.BASIC_SINGLE_ENTITY_CACHE_KEY_SPEL;
import static it.lbsoftware.daily.config.Constants.DO_NOT_STORE_NULL_SPEL;
import static it.lbsoftware.daily.config.Constants.NOTE_CACHE;
import static it.lbsoftware.daily.config.Constants.TAG_CACHE;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exceptions.DailyNotFoundException;
import it.lbsoftware.daily.notes.Note;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
  @Transactional
  public TagDto createTag(@NonNull TagDto tag, @NonNull AppUser appUser) {
    Tag tagEntity = tagDtoMapper.convertToEntity(tag);
    tagEntity.setAppUser(appUser);
    Tag savedTagEntity = tagRepository.saveAndFlush(tagEntity);

    return tagDtoMapper.convertToDto(savedTagEntity);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(
      cacheNames = TAG_CACHE,
      key = BASIC_SINGLE_ENTITY_CACHE_KEY_SPEL,
      unless = DO_NOT_STORE_NULL_SPEL)
  public Optional<TagDto> readTag(@NonNull UUID uuid, @NonNull AppUser appUser) {
    return tagRepository.findByUuidAndAppUser(uuid, appUser).map(tagDtoMapper::convertToDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TagDto> readTags(Pageable pageable, @NonNull AppUser appUser) {
    return tagRepository.findByAppUser(pageable, appUser).map(tagDtoMapper::convertToDto);
  }

  @Override
  @Transactional
  @Caching(
      put = {
        @CachePut(
            cacheNames = TAG_CACHE,
            key = BASIC_SINGLE_ENTITY_CACHE_KEY_SPEL,
            unless = DO_NOT_STORE_NULL_SPEL)
      },
      evict = {@CacheEvict(cacheNames = NOTE_CACHE, allEntries = true)})
  public Optional<TagDto> updateTag(
      @NonNull UUID uuid, @NonNull TagDto tag, @NonNull AppUser appUser) {
    return tagRepository
        .findByUuidAndAppUser(uuid, appUser)
        .map(
            prevTag -> {
              prevTag.setName(tag.getName());
              prevTag.setColorHex(tag.getColorHex());
              return tagRepository.saveAndFlush(prevTag);
            })
        .map(tagDtoMapper::convertToDto);
  }

  @Override
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(cacheNames = TAG_CACHE, key = BASIC_SINGLE_ENTITY_CACHE_KEY_SPEL),
        @CacheEvict(cacheNames = NOTE_CACHE, allEntries = true)
      })
  public void deleteTag(@NonNull UUID uuid, @NonNull AppUser appUser) {
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
