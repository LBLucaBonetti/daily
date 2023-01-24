package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exception.DailyException;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagService;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

  private final NoteRepository noteRepository;
  private final TagService tagService;
  private final NoteDtoMapper noteDtoMapper;

  @Override
  public NoteDto createNote(@NonNull NoteDto note, @NonNull String appUser) {
    Note noteEntity = noteDtoMapper.convertToEntity(note);
    noteEntity.setAppUser(appUser);
    Note savedNoteEntity = noteRepository.save(noteEntity);

    return noteDtoMapper.convertToDto(savedNoteEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<NoteDto> readNote(@NonNull UUID uuid, @NonNull String appUser) {
    return noteRepository.findByUuidAndAppUser(uuid, appUser).map(noteDtoMapper::convertToDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<NoteDto> readNotes(Pageable pageable, @NonNull String appUser) {
    return noteRepository.findByAppUser(pageable, appUser).map(noteDtoMapper::convertToDto);
  }

  @Override
  @Transactional
  public Optional<Note> updateNote(
      @NonNull UUID uuid, @NonNull Note note, @NonNull String appUser) {
    return noteRepository
        .findByUuidAndAppUser(uuid, appUser)
        .map(
            prevNote -> {
              prevNote.setText(note.getText());
              return noteRepository.save(prevNote);
            });
  }

  @Override
  @Transactional
  public Boolean deleteNote(@NonNull UUID uuid, @NonNull String appUser) {
    Optional<Note> noteOptional = noteRepository.findByUuidAndAppUser(uuid, appUser);
    if (noteOptional.isEmpty()) {
      return false;
    }
    Note note = noteOptional.get();
    noteRepository.delete(note);

    return true;
  }

  @Override
  @Transactional
  public Boolean addTagToNote(@NonNull UUID uuid, @NonNull UUID tagUuid, @NonNull String appUser) {
    Optional<Note> noteOptional = noteRepository.findByUuidAndAppUser(uuid, appUser);
    if (noteOptional.isEmpty()) {
      return false;
    }
    Optional<Tag> tagOptional = tagService.readTag(tagUuid, appUser);
    if (tagOptional.isEmpty()) {
      return false;
    }
    Note note = noteOptional.get();
    Tag tag = tagOptional.get();
    if (note.getTags().size() >= Constants.NOTE_TAGS_MAX) {
      throw new DailyException(Constants.ERROR_NOTE_TAGS_MAX);
    }
    tag.addToNote(note);
    noteRepository.save(note);

    return true;
  }

  @Override
  @Transactional
  public Boolean removeTagFromNote(
      @NonNull UUID uuid, @NonNull UUID tagUuid, @NonNull String appUser) {
    Optional<Note> noteOptional = noteRepository.findByUuidAndAppUser(uuid, appUser);
    if (noteOptional.isEmpty()) {
      return false;
    }
    Optional<Tag> tagOptional = tagService.readTag(tagUuid, appUser);
    if (tagOptional.isEmpty()) {
      return false;
    }
    Note note = noteOptional.get();
    Tag tag = tagOptional.get();
    tag.removeFromNote(note);
    noteRepository.save(note);

    return true;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Set<Tag>> readNoteTags(@NonNull UUID uuid, @NonNull String appUser) {
    return noteRepository.findByUuidAndAppUserFetchTags(uuid, appUser).map(Note::getTags);
  }
}
