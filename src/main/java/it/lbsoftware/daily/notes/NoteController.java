package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
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
    log.info("POST request to /api/notes; parameters: %s".formatted(noteDto.toString()));
    NoteDto createdNoteDto = noteService.createNote(noteDto, appUserService.getAppUser(principal));

    return ResponseEntity.status(HttpStatus.CREATED).body(createdNoteDto);
  }

  @GetMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> readNote(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal Object principal) {
    log.info("GET request to /api/notes/%s".formatted(uuid.toString()));
    return noteService
        .readNote(uuid, appUserService.getAppUser(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<PageDto<NoteDto>> readNotes(
      Pageable pageable, @AuthenticationPrincipal Object principal) {
    log.info("GET request to /api/notes with paging");
    Page<NoteDto> readNotes;
    try {
      readNotes = noteService.readNotes(pageable, appUserService.getAppUser(principal));
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
    log.info(
        "PUT request to /api/notes/%s; parameters: %s"
            .formatted(uuid.toString(), noteDto.toString()));
    return noteService
        .updateNote(uuid, noteDto, appUserService.getAppUser(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> deleteNote(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal Object principal) {
    log.info("DELETE request to /api/notes/%s".formatted(uuid.toString()));
    noteService.deleteNote(uuid, appUserService.getAppUser(principal));

    return ResponseEntity.noContent().build();
  }

  @PutMapping(value = "/{uuid}/tags/{tagUuid}")
  public ResponseEntity<TagDto> addTagToNote(
      @PathVariable("uuid") UUID uuid,
      @PathVariable("tagUuid") UUID tagUuid,
      @AuthenticationPrincipal Object principal) {
    log.info("PUT request to /api/notes/%s/tags/%s".formatted(uuid.toString(), tagUuid.toString()));
    noteService.addTagToNote(uuid, tagUuid, appUserService.getAppUser(principal));

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/{uuid}/tags/{tagUuid}")
  public ResponseEntity<TagDto> removeTagFromNote(
      @PathVariable("uuid") UUID uuid,
      @PathVariable("tagUuid") UUID tagUuid,
      @AuthenticationPrincipal Object principal) {
    log.info(
        "DELETE request to /api/notes/%s/tags/%s".formatted(uuid.toString(), tagUuid.toString()));
    noteService.removeTagFromNote(uuid, tagUuid, appUserService.getAppUser(principal));

    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/{uuid}/tags")
  public ResponseEntity<Set<TagDto>> readNoteTags(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal Object principal) {
    log.info("GET request to /api/notes/%s/tags".formatted(uuid.toString()));
    return noteService
        .readNoteTags(uuid, appUserService.getAppUser(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
