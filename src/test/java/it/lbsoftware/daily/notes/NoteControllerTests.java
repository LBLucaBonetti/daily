package it.lbsoftware.daily.notes;

import static it.lbsoftware.daily.notes.NoteTestUtils.createNoteDto;
import static it.lbsoftware.daily.tags.TagTestUtils.createTagDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exception.DailyBadRequestException;
import it.lbsoftware.daily.exception.DailyConflictException;
import it.lbsoftware.daily.exception.DailyNotFoundException;
import it.lbsoftware.daily.tags.TagDto;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@DisplayName("NoteController unit tests")
class NoteControllerTests extends DailyAbstractUnitTests {

  private static final String TEXT = "text";
  private static final UUID APP_USER = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  @Mock private NoteService noteService;
  @Mock private AppUserService appUserService;
  @Mock private OidcUser appUser;
  @Mock private Pageable pageable;
  private NoteController noteController;

  @BeforeEach
  void beforeEach() {
    noteController = new NoteController(noteService, appUserService);
    given(appUserService.getUuid(appUser)).willReturn(APP_USER);
  }

  @Test
  @DisplayName("Should create note and return created")
  void test1() {
    // Given
    NoteDto noteDto = createNoteDto(null, TEXT);
    NoteDto createdNoteDto = createNoteDto(UUID.randomUUID(), TEXT);
    given(noteService.createNote(noteDto, APP_USER)).willReturn(createdNoteDto);

    // When
    ResponseEntity<NoteDto> res = noteController.createNote(noteDto, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).createNote(noteDto, APP_USER);
    assertEquals(HttpStatus.CREATED, res.getStatusCode());
    assertEquals(createdNoteDto, res.getBody());
  }

  @Test
  @DisplayName("Should not read note and return not found")
  void test2() {
    // Given
    Optional<NoteDto> readNote = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteService.readNote(uuid, APP_USER)).willReturn(readNote);

    // When
    ResponseEntity<NoteDto> res = noteController.readNote(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).readNote(uuid, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should read note and return ok")
  void test3() {
    // Given
    UUID uuid = UUID.randomUUID();
    Optional<NoteDto> readNote = Optional.of(createNoteDto(uuid, TEXT));
    given(noteService.readNote(uuid, APP_USER)).willReturn(readNote);

    // When
    ResponseEntity<NoteDto> res = noteController.readNote(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).readNote(uuid, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(readNote.get(), res.getBody());
  }

  @Test
  @DisplayName("Should read notes and return ok")
  void test4() {
    // Given
    NoteDto noteDto = createNoteDto(UUID.randomUUID(), TEXT);
    Page<NoteDto> readNotes = new PageImpl<>(List.of(noteDto));
    given(noteService.readNotes(pageable, APP_USER)).willReturn(readNotes);

    // When
    ResponseEntity<PageDto<NoteDto>> res = noteController.readNotes(pageable, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).readNotes(pageable, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(res.getBody());
    assertNotNull(res.getBody().getContent());
    assertEquals(readNotes.getContent().size(), res.getBody().getContent().size());
    assertEquals(noteDto, res.getBody().getContent().get(0));
  }

  @Test
  @DisplayName("Should not update note and return not found")
  void test5() {
    // Given
    UUID uuid = UUID.randomUUID();
    NoteDto noteDto = createNoteDto(uuid, TEXT);
    Optional<NoteDto> updatedNoteDto = Optional.empty();
    given(noteService.updateNote(uuid, noteDto, APP_USER)).willReturn(updatedNoteDto);

    // When
    ResponseEntity<NoteDto> res = noteController.updateNote(uuid, noteDto, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).updateNote(uuid, noteDto, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should update note and return ok")
  void test6() {
    // Given
    UUID uuid = UUID.randomUUID();
    NoteDto noteDto = createNoteDto(uuid, TEXT);
    Optional<NoteDto> updatedNote = Optional.of(noteDto);
    given(noteService.updateNote(uuid, noteDto, APP_USER)).willReturn(updatedNote);

    // When
    ResponseEntity<NoteDto> res = noteController.updateNote(uuid, noteDto, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).updateNote(uuid, noteDto, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(noteDto, res.getBody());
  }

  @Test
  @DisplayName("Should not delete note and throw not found")
  void test7() {
    // Given
    UUID uuid = UUID.randomUUID();
    doThrow(new DailyNotFoundException(Constants.ERROR_NOT_FOUND))
        .when(noteService)
        .deleteNote(uuid, APP_USER);

    // When
    DailyNotFoundException res =
        assertThrows(DailyNotFoundException.class, () -> noteController.deleteNote(uuid, appUser));

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).deleteNote(uuid, APP_USER);
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should delete note and return no content")
  void test8() {
    // Given
    UUID uuid = UUID.randomUUID();
    doNothing().when(noteService).deleteNote(uuid, APP_USER);

    // When
    ResponseEntity<NoteDto> res = noteController.deleteNote(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).deleteNote(uuid, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should not add tag to note and throw not found")
  void test9() {
    // Given
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    doThrow(new DailyNotFoundException(Constants.ERROR_NOT_FOUND))
        .when(noteService)
        .addTagToNote(uuid, tagUuid, APP_USER);

    // When
    DailyNotFoundException res =
        assertThrows(
            DailyNotFoundException.class,
            () -> noteController.addTagToNote(uuid, tagUuid, appUser));

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).addTagToNote(uuid, tagUuid, APP_USER);
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should add tag to note and return no content")
  void test10() {
    // Given
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    doNothing().when(noteService).addTagToNote(uuid, tagUuid, APP_USER);

    // When
    ResponseEntity<TagDto> res = noteController.addTagToNote(uuid, tagUuid, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).addTagToNote(uuid, tagUuid, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should not remove tag from note and return not found")
  void test11() {
    // Given
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    doThrow(new DailyNotFoundException(Constants.ERROR_NOT_FOUND))
        .when(noteService)
        .removeTagFromNote(uuid, tagUuid, APP_USER);

    // When
    DailyNotFoundException res =
        assertThrows(
            DailyNotFoundException.class,
            () -> noteController.removeTagFromNote(uuid, tagUuid, appUser));

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).removeTagFromNote(uuid, tagUuid, APP_USER);
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should remove tag from note and return no content")
  void test12() {
    // Given
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    doNothing().when(noteService).removeTagFromNote(uuid, tagUuid, APP_USER);

    // When
    ResponseEntity<TagDto> res = noteController.removeTagFromNote(uuid, tagUuid, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).removeTagFromNote(uuid, tagUuid, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should not read note tags and return not found")
  void test13() {
    // Given
    Optional<Set<TagDto>> readNoteTags = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteService.readNoteTags(uuid, APP_USER)).willReturn(readNoteTags);

    // When
    ResponseEntity<Set<TagDto>> res = noteController.readNoteTags(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).readNoteTags(uuid, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should read note tags and return ok")
  void test14() {
    // Given
    UUID uuid = UUID.randomUUID();
    Set<TagDto> readNoteTagDtos = Set.of(createTagDto(uuid, NAME, COLOR_HEX));
    given(noteService.readNoteTags(uuid, APP_USER)).willReturn(Optional.of(readNoteTagDtos));

    // When
    ResponseEntity<Set<TagDto>> res = noteController.readNoteTags(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUuid(appUser);
    verify(noteService, times(1)).readNoteTags(uuid, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(readNoteTagDtos, res.getBody());
  }

  @Test
  @DisplayName(
      "Should not read notes because of wrong note field name as sort parameter and return bad request")
  void test15() {
    // Given
    doThrow(new RuntimeException("Wrong field name as sort parameter"))
        .when(noteService)
        .readNotes(pageable, APP_USER);

    // When
    DailyBadRequestException res =
        assertThrows(
            DailyBadRequestException.class, () -> noteController.readNotes(pageable, appUser));

    // Then
    assertNotNull(res);
    verify(noteService, times(1)).readNotes(pageable, APP_USER);
    assertNull(res.getMessage());
  }

  @Test
  @DisplayName("Should not add tag to note because of note tag limits and return conflict")
  void test16() {
    // Given
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    doThrow(new DailyConflictException(Constants.ERROR_NOTE_TAGS_MAX))
        .when(noteService)
        .addTagToNote(uuid, tagUuid, APP_USER);

    // When
    DailyConflictException res =
        assertThrows(
            DailyConflictException.class,
            () -> noteController.addTagToNote(uuid, tagUuid, appUser));

    // Then
    assertNotNull(res);
    assertEquals(Constants.ERROR_NOTE_TAGS_MAX, res.getMessage());
  }
}
