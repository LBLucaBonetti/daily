package it.lbsoftware.daily.notes;

import static it.lbsoftware.daily.notes.NoteTestUtils.createNote;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagService;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@DisplayName("NoteServiceImpl unit tests")
class NoteServiceImplTests extends DailyAbstractUnitTests {

  @Mock private NoteRepository noteRepository;
  @Mock private TagService tagService;
  private NoteServiceImpl noteService;
  private static final String TEXT = "text";
  private static final String APP_USER = "appUser";
  private static final String OTHER_TEXT = "otherText";
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";

  @BeforeEach
  void beforeEach() {
    noteService = new NoteServiceImpl(noteRepository, tagService);
  }

  @Test
  @DisplayName("Should create note and return note")
  void test1() {
    // Given
    Note note = createNote(TEXT, Collections.emptySet(), null);
    Note createdNote = createNote(TEXT, Collections.emptySet(), APP_USER);
    given(noteRepository.save(note)).willReturn(createdNote);

    // When
    Note res = noteService.createNote(note, APP_USER);

    // Then
    verify(noteRepository, times(1)).save(note);
    assertEquals(APP_USER, res.getAppUser());
  }

  @Test
  @DisplayName("Should not read note and return empty optional")
  void test2() {
    // Given
    Optional<Note> note = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(note);

    // When
    Optional<Note> res = noteService.readNote(uuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    assertEquals(note, res);
  }

  @Test
  @DisplayName("Should read note and return note optional")
  void test3() {
    // Given
    Optional<Note> note = Optional.of(createNote(TEXT, Collections.emptySet(), APP_USER));
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(note);

    // When
    Optional<Note> res = noteService.readNote(uuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    assertEquals(note, res);
  }

  @Test
  @DisplayName("Should not read notes and return empty list")
  void test4() {
    // Given
    List<Note> notes = Collections.emptyList();
    given(noteRepository.findByAppUser(APP_USER)).willReturn(notes);

    // When
    List<Note> res = noteService.readNotes(APP_USER);

    // Then
    verify(noteRepository, times(1)).findByAppUser(APP_USER);
    assertEquals(notes, res);
  }

  @Test
  @DisplayName("Should read notes and return note list")
  void test5() {
    // Given
    List<Note> notes = List.of(createNote(TEXT, Collections.emptySet(), APP_USER));
    given(noteRepository.findByAppUser(APP_USER)).willReturn(notes);

    // When
    List<Note> res = noteService.readNotes(APP_USER);

    // Then
    verify(noteRepository, times(1)).findByAppUser(APP_USER);
    assertEquals(notes, res);
  }

  @Test
  @DisplayName("Should not update note and return empty optional")
  void test6() {
    // Given
    Optional<Note> noteOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);

    // When
    Optional<Note> res =
        noteService.updateNote(uuid, createNote(TEXT, Collections.emptySet(), APP_USER), APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(noteRepository, times(0)).save(any());
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName("Should update note and return note optional")
  void test7() {
    // Given
    Note prevNote = createNote(TEXT, Collections.emptySet(), APP_USER);
    Note updatedNote = createNote(OTHER_TEXT, Collections.emptySet(), APP_USER);
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(Optional.of(prevNote));
    given(noteRepository.save(prevNote)).willReturn(updatedNote);

    // When
    Optional<Note> res =
        noteService.updateNote(
            uuid, createNote(OTHER_TEXT, Collections.emptySet(), null), APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(noteRepository, times(1)).save(prevNote);
    assertEquals(res, Optional.of(updatedNote));
    assertEquals(OTHER_TEXT, res.get().getText());
  }

  @Test
  @DisplayName("Should not delete note and return false")
  void test8() {
    // Given
    Optional<Note> noteOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);

    // When
    Boolean res = noteService.deleteNote(uuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(noteRepository, times(0)).delete(any());
    assertEquals(Boolean.FALSE, res);
  }

  @Test
  @DisplayName("Should delete note and return true")
  void test9() {
    // Given
    Optional<Note> noteOptional = Optional.of(createNote(TEXT, Collections.emptySet(), APP_USER));
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);

    // When
    Boolean res = noteService.deleteNote(uuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(noteRepository, times(1)).delete(noteOptional.get());
    assertEquals(Boolean.TRUE, res);
  }

  @Test
  @DisplayName("Should not add tag to note because of note not found and return false")
  void test10() {
    // Given
    Optional<Note> noteOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);

    // When
    Boolean res = noteService.addTagToNote(uuid, UUID.randomUUID(), APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagService, times(0)).readTag(any(), any());
    verify(noteRepository, times(0)).save(any());
    assertEquals(Boolean.FALSE, res);
  }

  @Test
  @DisplayName("Should not add tag to note because of tag not found and return false")
  void test11() {
    // Given
    Optional<Note> noteOptional = Optional.of(createNote(TEXT, Collections.emptySet(), APP_USER));
    Optional<Tag> tagOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);
    given(tagService.readTag(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    Boolean res = noteService.addTagToNote(uuid, tagUuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagService, times(1)).readTag(tagUuid, APP_USER);
    verify(noteRepository, times(0)).save(any());
    assertEquals(Boolean.FALSE, res);
  }

  @Test
  @DisplayName("Should add tag to note and return true")
  void test12() {
    // Given
    Note note = createNote(TEXT, new HashSet<>(), APP_USER);
    Optional<Note> noteOptional = Optional.of(note);
    Tag tag = createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER);
    Optional<Tag> tagOptional = Optional.of(tag);
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);
    given(tagService.readTag(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    Boolean res = noteService.addTagToNote(uuid, tagUuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagService, times(1)).readTag(tagUuid, APP_USER);
    verify(noteRepository, times(1)).save(note);
    assertTrue(note.getTagSet().contains(tag));
    assertTrue(tag.getNoteSet().contains(note));
    assertEquals(Boolean.TRUE, res);
  }

  @Test
  @DisplayName("Should not remove tag from note because of note not found and return false")
  void test13() {
    // Given
    Optional<Note> noteOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);

    // When
    Boolean res = noteService.removeTagFromNote(uuid, UUID.randomUUID(), APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagService, times(0)).readTag(any(), any());
    verify(noteRepository, times(0)).save(any());
    assertEquals(Boolean.FALSE, res);
  }

  @Test
  @DisplayName("Should not remove tag from note because of tag not found and return false")
  void test14() {
    // Given
    Optional<Note> noteOptional = Optional.of(createNote(TEXT, Collections.emptySet(), APP_USER));
    Optional<Tag> tagOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);
    given(tagService.readTag(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    Boolean res = noteService.removeTagFromNote(uuid, tagUuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagService, times(1)).readTag(tagUuid, APP_USER);
    verify(noteRepository, times(0)).save(any());
    assertEquals(Boolean.FALSE, res);
  }

  @Test
  @DisplayName("Should remove tag from note and return true")
  void test15() {
    // Given
    Note note = createNote(TEXT, new HashSet<>(), APP_USER);
    Optional<Note> noteOptional = Optional.of(note);
    Tag tag = createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER);
    Optional<Tag> tagOptional = Optional.of(tag);
    tag.addToNote(note);
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);
    given(tagService.readTag(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    Boolean res = noteService.removeTagFromNote(uuid, tagUuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagService, times(1)).readTag(tagUuid, APP_USER);
    verify(noteRepository, times(1)).save(note);
    assertFalse(note.getTagSet().contains(tag));
    assertFalse(tag.getNoteSet().contains(note));
    assertEquals(Boolean.TRUE, res);
  }

  @Test
  @DisplayName("Should not read note tags and return empty optional")
  void test16() {
    // Given
    Optional<Note> noteOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUserFetchTags(uuid, APP_USER)).willReturn(noteOptional);

    // When
    Optional<Set<Tag>> res = noteService.readNoteTags(uuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUserFetchTags(uuid, APP_USER);
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName("Should read note tags and return tag set optional")
  void test17() {
    // Given
    Set<Tag> tagSet = Set.of(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    Optional<Note> noteOptional = Optional.of(createNote(TEXT, tagSet, APP_USER));
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUserFetchTags(uuid, APP_USER)).willReturn(noteOptional);

    // When
    Optional<Set<Tag>> res = noteService.readNoteTags(uuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUserFetchTags(uuid, APP_USER);
    assertEquals(Optional.of(tagSet), res);
  }
}
