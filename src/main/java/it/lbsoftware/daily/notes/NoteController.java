package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/notes")
class NoteController {

  private final NoteService noteService;
  private final NoteDtoMapper noteDtoMapper;
  private final TagDtoMapper tagDtoMapper;
  private final AppUserService appUserService;

  @PostMapping
  public ResponseEntity<NoteDto> createNote(
      @Valid @RequestBody NoteDto noteDto, @AuthenticationPrincipal OidcUser appUser) {
    NoteDto createdNoteDto = noteService.createNote(noteDto, appUserService.getUid(appUser));

    return ResponseEntity.status(HttpStatus.CREATED).body(createdNoteDto);
  }

  @GetMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> readNote(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal OidcUser appUser) {
    return noteService
        .readNote(uuid, appUserService.getUid(appUser))
        .map(readNote -> ResponseEntity.ok(noteDtoMapper.convertToDto(readNote)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<PageDto<NoteDto>> readNotes(
      Pageable pageable, @AuthenticationPrincipal OidcUser appUser) {
    Page<Note> readNotes;
    try {
      readNotes = noteService.readNotes(pageable, appUserService.getUid(appUser));
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
    }
    PageDto<NoteDto> readNoteDtos = new PageDto<>(readNotes.map(noteDtoMapper::convertToDto));

    return ResponseEntity.ok(readNoteDtos);
  }

  @PutMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> updateNote(
      @PathVariable("uuid") UUID uuid,
      @Valid @RequestBody NoteDto noteDto,
      @AuthenticationPrincipal OidcUser appUser) {
    Note note = noteDtoMapper.convertToEntity(noteDto);

    return noteService
        .updateNote(uuid, note, appUserService.getUid(appUser))
        .<ResponseEntity<NoteDto>>map(updatedNote -> ResponseEntity.noContent().build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping(value = "/{uuid}")
  public ResponseEntity<NoteDto> deleteNote(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal OidcUser appUser) {
    if (!Boolean.TRUE.equals(noteService.deleteNote(uuid, appUserService.getUid(appUser)))) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }

  @PutMapping(value = "/{uuid}/tags/{tagUuid}")
  public ResponseEntity<TagDto> addTagToNote(
      @PathVariable("uuid") UUID uuid,
      @PathVariable("tagUuid") UUID tagUuid,
      @AuthenticationPrincipal OidcUser appUser) {
    try {
      if (!Boolean.TRUE.equals(
          noteService.addTagToNote(uuid, tagUuid, appUserService.getUid(appUser)))) {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
    }

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/{uuid}/tags/{tagUuid}")
  public ResponseEntity<TagDto> removeTagFromNote(
      @PathVariable("uuid") UUID uuid,
      @PathVariable("tagUuid") UUID tagUuid,
      @AuthenticationPrincipal OidcUser appUser) {
    if (!Boolean.TRUE.equals(
        noteService.removeTagFromNote(uuid, tagUuid, appUserService.getUid(appUser)))) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }

  @GetMapping(value = "/{uuid}/tags")
  public ResponseEntity<Set<TagDto>> readNoteTags(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal OidcUser appUser) {
    return noteService
        .readNoteTags(uuid, appUserService.getUid(appUser))
        .map(readNoteTags -> ResponseEntity.ok(tagDtoMapper.convertToDto(readNoteTags)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
