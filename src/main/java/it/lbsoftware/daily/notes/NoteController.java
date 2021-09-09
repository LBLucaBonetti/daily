package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final NoteDtoMapper noteDtoMapper;
    private final TagDtoMapper tagDtoMapper;

    @PostMapping
    public ResponseEntity<NoteDto> createNote(@Valid @RequestBody NoteDto noteDto) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if (appUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AppUser appUser = appUserOptional.get();
        Note note = noteDtoMapper.convertToEntity(noteDto);
        Note createdNote = noteService.createNote(note, appUser);
        NoteDto createdNoteDto = noteDtoMapper.convertToDto(createdNote);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdNoteDto);
    }

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<NoteDto> readNote(@PathVariable("uuid") UUID uuid) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if (appUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AppUser appUser = appUserOptional.get();
        Optional<Note> readNote = noteService.readNote(uuid, appUser);
        if (readNote.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        NoteDto readNoteDto = noteDtoMapper.convertToDto(readNote.get());

        return ResponseEntity.ok(readNoteDto);
    }

    @GetMapping
    public ResponseEntity<List<NoteDto>> readNotes() {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if (appUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AppUser appUser = appUserOptional.get();
        List<Note> readNotes = noteService.readNotes(appUser);
        List<NoteDto> readNoteDtos = readNotes.stream().map(noteDtoMapper::convertToDto).collect(Collectors.toList());

        return ResponseEntity.ok(readNoteDtos);
    }

    @PutMapping(value = "/{uuid}")
    public ResponseEntity<NoteDto> updateNote(@PathVariable("uuid") UUID uuid, @Valid @RequestBody NoteDto noteDto) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if (appUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AppUser appUser = appUserOptional.get();
        Note note = noteDtoMapper.convertToEntity(noteDto);
        Optional<Note> updatedNote = noteService.updateNote(uuid, note, appUser);
        if (updatedNote.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<NoteDto> deleteNote(@PathVariable("uuid") UUID uuid) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if (appUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AppUser appUser = appUserOptional.get();
        if (!noteService.deleteNote(uuid, appUser)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{uuid}/tags/{tagUuid}")
    public ResponseEntity<TagDto> addTagToNote(@PathVariable("uuid") UUID uuid, @PathVariable("tagUuid") UUID tagUuid) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if (appUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AppUser appUser = appUserOptional.get();
        if (!noteService.addTagToNote(uuid, tagUuid, appUser)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{uuid}/tags/{tagUuid}")
    public ResponseEntity<TagDto> removeTagFromNote(@PathVariable("uuid") UUID uuid, @PathVariable("tagUuid") UUID tagUuid) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if (appUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AppUser appUser = appUserOptional.get();
        if (!noteService.removeTagFromNote(uuid, tagUuid, appUser)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{uuid}/tags")
    public ResponseEntity<Set<TagDto>> readNoteTags(@PathVariable("uuid") UUID uuid) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if (appUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AppUser appUser = appUserOptional.get();
        Optional<Set<Tag>> readNoteTags = noteService.readNoteTags(uuid, appUser);
        if (readNoteTags.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Set<TagDto> readNoteTagDtos = readNoteTags.get().stream().map(tagDtoMapper::convertToDto).collect(Collectors.toSet());

        return ResponseEntity.ok(readNoteTagDtos);
    }

}
