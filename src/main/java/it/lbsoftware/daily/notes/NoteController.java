package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.entities.DtoEntityMappingConverter;
import it.lbsoftware.daily.tags.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
// TODO Change @CrossOrigin to match the deployed frontend URL
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping(value = "/notes")
class NoteController {

    private final AppUserService appUserService;
    private final NoteService noteService;
    private final DtoEntityMappingConverter<NoteDtoIn, Note> noteInConverter;
    private final DtoEntityMappingConverter<NoteDtoOut, Note> noteOutConverter;
    private final TagService tagService;

    @PostMapping
    public ResponseEntity<NoteDtoOut> createNote(@Valid @RequestBody NoteDtoIn noteDtoIn) {
        AppUser appUser = appUserService.getAppUserFromToken();
        Note note = noteInConverter.convertToEntity(noteDtoIn, Note.class);
        noteDtoIn.getTagSet().forEach(tagUuid -> tagService.readTag(tagUuid, appUser).ifPresent(value -> note.getTagSet().add(value)));
        Note createdNote = noteService.createNote(note, appUser);
        NoteDtoOut createdNoteDtoOut = noteOutConverter.convertToDto(createdNote, NoteDtoOut.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdNoteDtoOut);
    }

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<NoteDtoOut> readNote(@PathVariable("uuid") UUID uuid) {
        AppUser appUser = appUserService.getAppUserFromToken();
        Optional<Note> readNote = noteService.readNote(uuid, appUser);
        if (readNote.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        NoteDtoOut readNoteDtoOut = noteOutConverter.convertToDto(readNote.get(), NoteDtoOut.class);

        return ResponseEntity.ok(readNoteDtoOut);
    }

    @GetMapping
    public ResponseEntity<List<NoteDtoOut>> readNotes() {
        AppUser appUser = appUserService.getAppUserFromToken();
        List<Note> readNotes = noteService.readNotes(appUser);
        List<NoteDtoOut> readNoteDtoOuts = readNotes.stream().map(readNote -> noteOutConverter.convertToDto(readNote, NoteDtoOut.class)).collect(Collectors.toList());

        return ResponseEntity.ok(readNoteDtoOuts);
    }

    @PutMapping(value = "/{uuid}")
    public ResponseEntity<NoteDtoOut> updateNote(@PathVariable("uuid") UUID uuid, @Valid @RequestBody NoteDtoIn noteDtoIn) {
        AppUser appUser = appUserService.getAppUserFromToken();
        Note note = noteInConverter.convertToEntity(noteDtoIn, Note.class);
        noteDtoIn.getTagSet().forEach(tagUuid -> tagService.readTag(tagUuid, appUser).ifPresent(value -> note.getTagSet().add(value)));
        Optional<Note> updatedNote = noteService.updateNote(uuid, note, appUser);
        if (updatedNote.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<NoteDtoOut> deleteNote(@PathVariable("uuid") UUID uuid) {
        AppUser appUser = appUserService.getAppUserFromToken();
        if (!noteService.deleteNote(uuid, appUser)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

}
