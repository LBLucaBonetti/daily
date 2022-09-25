package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/notes")
class NoteController {

  private final NoteService noteService;
  private final NoteDtoMapper noteDtoMapper;
  private final TagDtoMapper tagDtoMapper;
  private final AppUserService appUserService;

  @PostMapping
  public ResponseEntity<NoteDto> createNote(@Valid @RequestBody NoteDto noteDto, @AuthenticationPrincipal OidcUser appUser) {
    Note note = noteDtoMapper.convertToEntity(noteDto);
    Note createdNote = noteService.createNote(note, appUserService.getUid(appUser));
    NoteDto createdNoteDto = noteDtoMapper.convertToDto(createdNote);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdNoteDto);
  }

  @GetMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> readNote(@PathVariable("uuid") UUID uuid, @AuthenticationPrincipal OidcUser appUser) {
    Optional<Note> readNote = noteService.readNote(uuid, appUserService.getUid(appUser));
    if (readNote.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    NoteDto readNoteDto = noteDtoMapper.convertToDto(readNote.get());

    return ResponseEntity.ok(readNoteDto);
  }

  @GetMapping
  public ResponseEntity<List<NoteDto>> readNotes(@AuthenticationPrincipal OidcUser appUser) {
    List<Note> readNotes = noteService.readNotes(appUserService.getUid(appUser));
    List<NoteDto> readNoteDtos =
        noteDtoMapper.convertToDto(readNotes);

    return ResponseEntity.ok(readNoteDtos);
  }

  @PutMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> updateNote(
      @PathVariable("uuid") UUID uuid, @Valid @RequestBody NoteDto noteDto, @AuthenticationPrincipal OidcUser appUser) {
    Note note = noteDtoMapper.convertToEntity(noteDto);
    Optional<Note> updatedNote = noteService.updateNote(uuid, note, appUserService.getUid(appUser));
    if (updatedNote.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> deleteNote(@PathVariable("uuid") UUID uuid, @AuthenticationPrincipal OidcUser appUser) {
    if (!Boolean.TRUE.equals(noteService.deleteNote(uuid, appUserService.getUid(appUser)))) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }

  @PutMapping(value = "/{uuid}/tags/{tagUuid}")
  public ResponseEntity<TagDto> addTagToNote(
      @PathVariable("uuid") UUID uuid, @PathVariable("tagUuid") UUID tagUuid, @AuthenticationPrincipal OidcUser appUser) {
    if (!Boolean.TRUE.equals(noteService.addTagToNote(uuid, tagUuid, appUserService.getUid(appUser)))) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/{uuid}/tags/{tagUuid}")
  public ResponseEntity<TagDto> removeTagFromNote(
      @PathVariable("uuid") UUID uuid, @PathVariable("tagUuid") UUID tagUuid, @AuthenticationPrincipal OidcUser appUser) {
    if (!Boolean.TRUE.equals(noteService.removeTagFromNote(uuid, tagUuid, appUserService.getUid(appUser)))) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/{uuid}/tags")
  public ResponseEntity<Set<TagDto>> readNoteTags(@PathVariable("uuid") UUID uuid, @AuthenticationPrincipal OidcUser appUser) {
    Optional<Set<Tag>> readNoteTags = noteService.readNoteTags(uuid, appUserService.getUid(appUser));
    if (readNoteTags.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    Set<TagDto> readNoteTagDtos =
        tagDtoMapper.convertToDto(readNoteTags.get());

    return ResponseEntity.ok(readNoteTagDtos);
  }
}
