package it.lbsoftware.daily.notes;

import static it.lbsoftware.daily.config.Constants.DO_NOT_STORE_NULL_SPEL;
import static it.lbsoftware.daily.config.Constants.NOTE_CACHE;
import static it.lbsoftware.daily.config.Constants.NOTE_TAGS_CACHE_KEY_SPEL;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exceptions.DailyConflictException;
import it.lbsoftware.daily.exceptions.DailyNotFoundException;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import it.lbsoftware.daily.tags.TagRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = NOTE_CACHE)
public class NoteServiceImpl implements NoteService {

  private final NoteRepository noteRepository;
  private final TagRepository tagRepository;
  private final NoteDtoMapper noteDtoMapper;
  private final TagDtoMapper tagDtoMapper;

  @Override
  @Transactional
  public NoteDto createNote(@NonNull NoteDto note, @NonNull AppUser appUser) {
    Note noteEntity = noteDtoMapper.convertToEntity(note);
    noteEntity.setAppUser(appUser);
    Note savedNoteEntity = noteRepository.saveAndFlush(noteEntity);

    return noteDtoMapper.convertToDto(savedNoteEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<NoteDto> readNote(@NonNull UUID uuid, @NonNull AppUser appUser) {
    return noteRepository.findByUuidAndAppUser(uuid, appUser).map(noteDtoMapper::convertToDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<NoteDto> readNotes(Pageable pageable, @NonNull AppUser appUser) {
    return noteRepository.findByAppUser(pageable, appUser).map(noteDtoMapper::convertToDto);
  }

  @Override
  @Transactional
  public Optional<NoteDto> updateNote(
      @NonNull UUID uuid, @NonNull NoteDto note, @NonNull AppUser appUser) {
    return noteRepository
        .findByUuidAndAppUser(uuid, appUser)
        .map(
            prevNote -> {
              prevNote.setText(note.getText());
              return noteRepository.saveAndFlush(prevNote);
            })
        .map(noteDtoMapper::convertToDto);
  }

  @Override
  @Transactional
  @CacheEvict(key = NOTE_TAGS_CACHE_KEY_SPEL)
  public void deleteNote(@NonNull UUID uuid, @NonNull AppUser appUser) {
    Note note =
        noteRepository
            .findByUuidAndAppUser(uuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    noteRepository.delete(note);
  }

  @Override
  @Transactional
  @CacheEvict(key = NOTE_TAGS_CACHE_KEY_SPEL)
  public void addTagToNote(@NonNull UUID uuid, @NonNull UUID tagUuid, @NonNull AppUser appUser) {
    Note note =
        noteRepository
            .findByUuidAndAppUser(uuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    Tag tag =
        tagRepository
            .findByUuidAndAppUser(tagUuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    if (note.getTags().size() >= Constants.NOTE_TAGS_MAX) {
      throw new DailyConflictException(Constants.ERROR_NOTE_TAGS_MAX);
    }
    tag.addToNote(note);
  }

  @Override
  @Transactional
  @CacheEvict(key = NOTE_TAGS_CACHE_KEY_SPEL)
  public void removeTagFromNote(
      @NonNull UUID uuid, @NonNull UUID tagUuid, @NonNull AppUser appUser) {
    Note note =
        noteRepository
            .findByUuidAndAppUser(uuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    Tag tag =
        tagRepository
            .findByUuidAndAppUser(tagUuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    tag.removeFromNote(note);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(key = NOTE_TAGS_CACHE_KEY_SPEL, unless = DO_NOT_STORE_NULL_SPEL)
  public Optional<Set<TagDto>> readNoteTags(@NonNull UUID uuid, @NonNull AppUser appUser) {
    return noteRepository
        .findByUuidAndAppUserFetchTags(uuid, appUser)
        .map(Note::getTags)
        .map(tagDtoMapper::convertToDto);
  }
}
