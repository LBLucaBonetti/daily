package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

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
        prevNote.setTagSet(note.getTagSet());

        return Optional.of(noteRepository.save(prevNote));
    }

    @Override
    public Boolean deleteNote(UUID uuid, AppUser appUser) {
        Optional<Note> noteOptional = noteRepository.findByUuidAndAppUser(uuid, appUser);
        if (noteOptional.isEmpty()) {
            return false;
        }
        noteRepository.delete(noteOptional.get());

        return true;
    }

}
