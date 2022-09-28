package it.lbsoftware.daily.notes;

import static it.lbsoftware.daily.notes.NoteTestUtils.createNote;
import static it.lbsoftware.daily.notes.NoteTestUtils.createNoteDto;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static it.lbsoftware.daily.tags.TagTestUtils.createTagDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@DisplayName("NoteController unit tests")
class NoteControllerTests extends DailyAbstractUnitTests {

  @Mock private NoteService noteService;
  @Mock private NoteDtoMapper noteDtoMapper;
  @Mock private TagDtoMapper tagDtoMapper;
  @Mock private AppUserService appUserService;
  @Mock private OidcUser appUser;
  private NoteController noteController;
  private static final String TEXT = "text";
  private static final String APP_USER = "appUser";
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";

  @BeforeEach
  void beforeEach() {
    noteController = new NoteController(noteService, noteDtoMapper, tagDtoMapper, appUserService);
    given(appUserService.getUid(appUser)).willReturn(APP_USER);
  }

  @Test
  @DisplayName("Should create note and return created")
  void test1() {
    // Given
    NoteDto noteDto = createNoteDto(null, TEXT);
    Note note = createNote(TEXT, Collections.emptySet(), APP_USER);
    Note createdNote = createNote(TEXT, Collections.emptySet(), APP_USER);
    NoteDto createdNoteDto = createNoteDto(UUID.randomUUID(), TEXT);
    given(noteDtoMapper.convertToEntity(noteDto)).willReturn(note);
    given(noteService.createNote(note, APP_USER)).willReturn(createdNote);
    given(noteDtoMapper.convertToDto(createdNote)).willReturn(createdNoteDto);

    // When
    ResponseEntity<NoteDto> res = noteController.createNote(noteDto, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteDtoMapper, times(1)).convertToEntity(noteDto);
    verify(noteService, times(1)).createNote(note, APP_USER);
    verify(noteDtoMapper, times(1)).convertToDto(createdNote);
    assertEquals(HttpStatus.CREATED, res.getStatusCode());
    assertEquals(createdNoteDto, res.getBody());
  }

  @Test
  @DisplayName("Should not read note and return not found")
  void test2() {
    // Given
    Optional<Note> readNote = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteService.readNote(uuid, APP_USER)).willReturn(readNote);

    // When
    ResponseEntity<NoteDto> res = noteController.readNote(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteService, times(1)).readNote(uuid, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should read note and return ok")
  void test3() {
    // Given
    Optional<Note> readNote = Optional.of(createNote(TEXT, Collections.emptySet(), APP_USER));
    UUID uuid = UUID.randomUUID();
    NoteDto readNoteDto = createNoteDto(uuid, TEXT);
    given(noteService.readNote(uuid, APP_USER)).willReturn(readNote);
    given(noteDtoMapper.convertToDto(readNote.get())).willReturn(readNoteDto);

    // When
    ResponseEntity<NoteDto> res = noteController.readNote(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteService, times(1)).readNote(uuid, APP_USER);
    verify(noteDtoMapper, times(1)).convertToDto(readNote.get());
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(readNoteDto, res.getBody());
  }

  @Test
  @DisplayName("Should read notes and return ok")
  void test4() {
    // Given
    List<Note> readNotes = List.of(createNote(TEXT, Collections.emptySet(), APP_USER));
    List<NoteDto> readNoteDtos = List.of(createNoteDto(UUID.randomUUID(), TEXT));
    given(noteService.readNotes(APP_USER)).willReturn(readNotes);
    given(noteDtoMapper.convertToDto(readNotes)).willReturn(readNoteDtos);

    // When
    ResponseEntity<List<NoteDto>> res = noteController.readNotes(appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteService, times(1)).readNotes(APP_USER);
    verify(noteDtoMapper, times(1)).convertToDto(readNotes);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(readNoteDtos, res.getBody());
  }

  @Test
  @DisplayName("Should not update note and return not found")
  void test5() {
    // Given
    Note note = createNote(TEXT, Collections.emptySet(), APP_USER);
    Optional<Note> updatedNote = Optional.empty();
    UUID uuid = UUID.randomUUID();
    NoteDto noteDto = createNoteDto(uuid, TEXT);
    given(noteDtoMapper.convertToEntity(noteDto)).willReturn(note);
    given(noteService.updateNote(uuid, note, APP_USER)).willReturn(updatedNote);

    // When
    ResponseEntity<NoteDto> res = noteController.updateNote(uuid, noteDto, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteDtoMapper, times(1)).convertToEntity(noteDto);
    verify(noteService, times(1)).updateNote(uuid, note, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should update note and return no content")
  void test6() {
    // Given
    Note note = createNote(TEXT, Collections.emptySet(), APP_USER);
    Optional<Note> updatedNote = Optional.of(note);
    UUID uuid = UUID.randomUUID();
    NoteDto noteDto = createNoteDto(uuid, TEXT);
    given(noteDtoMapper.convertToEntity(noteDto)).willReturn(note);
    given(noteService.updateNote(uuid, note, APP_USER)).willReturn(updatedNote);

    // When
    ResponseEntity<NoteDto> res = noteController.updateNote(uuid, noteDto, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteDtoMapper, times(1)).convertToEntity(noteDto);
    verify(noteService, times(1)).updateNote(uuid, note, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should not delete note and return not found")
  void test7() {
    // Given
    UUID uuid = UUID.randomUUID();
    given(noteService.deleteNote(uuid, APP_USER)).willReturn(Boolean.FALSE);

    // When
    ResponseEntity<NoteDto> res = noteController.deleteNote(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteService, times(1)).deleteNote(uuid, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should delete note and return no content")
  void test8() {
    // Given
    UUID uuid = UUID.randomUUID();
    given(noteService.deleteNote(uuid, APP_USER)).willReturn(Boolean.TRUE);

    // When
    ResponseEntity<NoteDto> res = noteController.deleteNote(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteService, times(1)).deleteNote(uuid, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should not add tag to note and return not found")
  void test9() {
    // Given
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteService.addTagToNote(uuid, tagUuid, APP_USER)).willReturn(Boolean.FALSE);

    // When
    ResponseEntity<TagDto> res = noteController.addTagToNote(uuid, tagUuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteService, times(1)).addTagToNote(uuid, tagUuid, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should add tag to note and return no content")
  void test10() {
    // Given
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteService.addTagToNote(uuid, tagUuid, APP_USER)).willReturn(Boolean.TRUE);

    // When
    ResponseEntity<TagDto> res = noteController.addTagToNote(uuid, tagUuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
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
    given(noteService.removeTagFromNote(uuid, tagUuid, APP_USER)).willReturn(Boolean.FALSE);

    // When
    ResponseEntity<TagDto> res = noteController.removeTagFromNote(uuid, tagUuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteService, times(1)).removeTagFromNote(uuid, tagUuid, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should remove tag from note and return no content")
  void test12() {
    // Given
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteService.removeTagFromNote(uuid, tagUuid, APP_USER)).willReturn(Boolean.TRUE);

    // When
    ResponseEntity<TagDto> res = noteController.removeTagFromNote(uuid, tagUuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteService, times(1)).removeTagFromNote(uuid, tagUuid, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should not read note tags and return not found")
  void test13() {
    // Given
    Optional<Set<Tag>> readNoteTags = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteService.readNoteTags(uuid, APP_USER)).willReturn(readNoteTags);

    // When
    ResponseEntity<Set<TagDto>> res = noteController.readNoteTags(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteService, times(1)).readNoteTags(uuid, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should read note tags and return ok")
  void test14() {
    // Given
    Optional<Set<Tag>> readNoteTags =
        Optional.of(Set.of(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER)));
    UUID uuid = UUID.randomUUID();
    Set<TagDto> readNoteTagDtos = Set.of(createTagDto(uuid, NAME, COLOR_HEX));
    given(noteService.readNoteTags(uuid, APP_USER)).willReturn(readNoteTags);
    given(tagDtoMapper.convertToDto(readNoteTags.get())).willReturn(readNoteTagDtos);

    // When
    ResponseEntity<Set<TagDto>> res = noteController.readNoteTags(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(noteService, times(1)).readNoteTags(uuid, APP_USER);
    verify(tagDtoMapper, times(1)).convertToDto(readNoteTags.get());
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(readNoteTagDtos, res.getBody());
  }
}
