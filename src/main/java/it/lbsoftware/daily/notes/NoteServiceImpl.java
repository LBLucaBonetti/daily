package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final TagService tagService;

    @Override
    public Note createNote(@NonNull Note note, @NonNull AppUser appUser) {
        note.setAppUser(appUser);

        return noteRepository.save(note);
    }

    @Override
    public Optional<Note> readNote(@NonNull UUID uuid, @NonNull AppUser appUser) {
        return noteRepository.findByUuidAndAppUser(uuid, appUser);
    }

    @Override
    public List<Note> readNotes(@NonNull AppUser appUser) {
        return noteRepository.findByAppUser(appUser);
    }

    @Override
    public Optional<Note> updateNote(@NonNull UUID uuid, @NonNull Note note, @NonNull AppUser appUser) {
        Optional<Note> noteOptional = noteRepository.findByUuidAndAppUser(uuid, appUser);
        if (noteOptional.isEmpty()) {
            return Optional.empty();
        }
        Note prevNote = noteOptional.get();
        prevNote.setText(note.getText());

        return Optional.of(noteRepository.save(prevNote));
    }

    @Override
    public Boolean deleteNote(@NonNull UUID uuid, @NonNull AppUser appUser) {
        Optional<Note> noteOptional = noteRepository.findByUuidAndAppUser(uuid, appUser);
        if (noteOptional.isEmpty()) {
            return false;
        }
        Note note = noteOptional.get();
        noteRepository.delete(note);

        return true;
    }

    @Override
    public Boolean addTagToNote(@NonNull UUID uuid, @NonNull UUID tagUuid, @NonNull AppUser appUser) {
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
        tag.addToNote(note);
        noteRepository.save(note);

        return true;
    }

    @Override
    public Boolean removeTagFromNote(@NonNull UUID uuid, @NonNull UUID tagUuid, @NonNull AppUser appUser) {
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
    public Optional<Set<Tag>> readNoteTags(@NonNull UUID uuid, @NonNull AppUser appUser) {
        Optional<Note> noteOptional = noteRepository.findByUuidAndAppUser(uuid, appUser);
        if (noteOptional.isEmpty()) {
            return Optional.empty();
        }
        Note note = noteOptional.get();

        return Optional.of(note.getTagSet());
    }

}