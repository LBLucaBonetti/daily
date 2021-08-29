package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final TagService tagService;

    @Override
    public Note createNote(Note note, AppUser appUser) {
        note.setAppUser(appUser);

        return noteRepository.save(note);
    }

    @Override
    public Optional<Note> readNote(UUID uuid, AppUser appUser) {
        return noteRepository.findByUuidAndAppUser(uuid, appUser);
    }

    @Override
    public List<Note> readNotes(AppUser appUser) {
        return noteRepository.findByAppUser(appUser);
    }

    @Override
    public Optional<Note> updateNote(UUID uuid, Note note, AppUser appUser) {
        Optional<Note> noteOptional = noteRepository.findByUuidAndAppUser(uuid, appUser);
        if (noteOptional.isEmpty()) {
            return Optional.empty();
        }
        Note prevNote = noteOptional.get();
        prevNote.setText(note.getText());

        return Optional.of(noteRepository.save(prevNote));
    }

    @Override
    public Boolean deleteNote(UUID uuid, AppUser appUser) {
        Optional<Note> noteOptional = noteRepository.findByUuidAndAppUser(uuid, appUser);
        if (noteOptional.isEmpty()) {
            return false;
        }
        Note note = noteOptional.get();
        noteRepository.delete(note);

        return true;
    }

    @Override
    public Boolean addTagToNote(UUID uuid, UUID tagUuid, AppUser appUser) {
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
        note.getTagSet().add(tag);
        noteRepository.save(note);

        return true;
    }

    @Override
    public Boolean removeTagFromNote(UUID uuid, UUID tagUuid, AppUser appUser) {
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
        note.getTagSet().remove(tag);
        noteRepository.save(note);

        return true;
    }

    @Override
    public Set<Tag> readNoteTags(UUID uuid, AppUser appUser) {
        Optional<Note> noteOptional = noteRepository.findByUuidAndAppUser(uuid, appUser);
        if (noteOptional.isEmpty()) {
            return Collections.emptySet();
        }
        Note note = noteOptional.get();

        return note.getTagSet();
    }

}
