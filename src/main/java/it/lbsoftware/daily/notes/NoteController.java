package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.exception.DailyBadRequestException;
import it.lbsoftware.daily.tags.TagDto;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@CommonsLog
class NoteController {

  private final NoteService noteService;
  private final AppUserService appUserService;

  @PostMapping
  public ResponseEntity<NoteDto> createNote(
      @Valid @RequestBody NoteDto noteDto, @AuthenticationPrincipal Object principal) {
    NoteDto createdNoteDto = noteService.createNote(noteDto, appUserService.getUuid(principal));

    return ResponseEntity.status(HttpStatus.CREATED).body(createdNoteDto);
  }

  @GetMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> readNote(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal Object principal) {
    return noteService
        .readNote(uuid, appUserService.getUuid(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<PageDto<NoteDto>> readNotes(
      Pageable pageable, @AuthenticationPrincipal Object principal) {
    Page<NoteDto> readNotes;
    try {
      readNotes = noteService.readNotes(pageable, appUserService.getUuid(principal));
    } catch (Exception e) {
      log.error(e);
      throw new DailyBadRequestException(null);
    }
    PageDto<NoteDto> readNoteDtos = new PageDto<>(readNotes);

    return ResponseEntity.ok(readNoteDtos);
  }

  @PutMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> updateNote(
      @PathVariable("uuid") UUID uuid,
      @Valid @RequestBody NoteDto noteDto,
      @AuthenticationPrincipal Object principal) {
    return noteService
        .updateNote(uuid, noteDto, appUserService.getUuid(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> deleteNote(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal Object principal) {
    noteService.deleteNote(uuid, appUserService.getUuid(principal));

    return ResponseEntity.noContent().build();
  }

  @PutMapping(value = "/{uuid}/tags/{tagUuid}")
  public ResponseEntity<TagDto> addTagToNote(
      @PathVariable("uuid") UUID uuid,
      @PathVariable("tagUuid") UUID tagUuid,
      @AuthenticationPrincipal Object principal) {
    noteService.addTagToNote(uuid, tagUuid, appUserService.getUuid(principal));

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/{uuid}/tags/{tagUuid}")
  public ResponseEntity<TagDto> removeTagFromNote(
      @PathVariable("uuid") UUID uuid,
      @PathVariable("tagUuid") UUID tagUuid,
      @AuthenticationPrincipal Object principal) {
    noteService.removeTagFromNote(uuid, tagUuid, appUserService.getUuid(principal));

    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/{uuid}/tags")
  public ResponseEntity<Set<TagDto>> readNoteTags(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal Object principal) {
    return noteService
        .readNoteTags(uuid, appUserService.getUuid(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
