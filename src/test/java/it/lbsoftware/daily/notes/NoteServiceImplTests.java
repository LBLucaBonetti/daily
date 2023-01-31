package it.lbsoftware.daily.notes;

import static it.lbsoftware.daily.notes.NoteTestUtils.createNote;
import static it.lbsoftware.daily.notes.NoteTestUtils.createNoteDto;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static it.lbsoftware.daily.tags.TagTestUtils.createTagDto;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exception.DailyConflictException;
import it.lbsoftware.daily.exception.DailyNotFoundException;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import it.lbsoftware.daily.tags.TagRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@DisplayName("NoteServiceImpl unit tests")
class NoteServiceImplTests extends DailyAbstractUnitTests {

  private static final String TEXT = "text";
  private static final String APP_USER = "appUser";
  private static final String OTHER_TEXT = "otherText";
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  @Mock private NoteRepository noteRepository;
  @Mock private TagRepository tagRepository;
  @Mock private Pageable pageable;
  @Mock private NoteDtoMapper noteDtoMapper;
  @Mock private TagDtoMapper tagDtoMapper;
  private NoteServiceImpl noteService;

  private static Stream<Arguments> test18() {
    // Note, appUser
    NoteDto note = createNoteDto(null, TEXT);
    return Stream.of(arguments(null, null), arguments(null, APP_USER), arguments(note, null));
  }

  private static Stream<Arguments> test19() {
    // Uuid, appUser
    UUID uuid = UUID.randomUUID();
    return Stream.of(arguments(null, null), arguments(null, APP_USER), arguments(uuid, null));
  }

  private static Stream<Arguments> test21() {
    // Uuid, note, appUser
    UUID uuid = UUID.randomUUID();
    NoteDto note = createNoteDto(uuid, TEXT);
    return Stream.of(
        arguments(null, null, null),
        arguments(null, null, APP_USER),
        arguments(null, note, null),
        arguments(null, note, APP_USER),
        arguments(uuid, null, null),
        arguments(uuid, null, APP_USER),
        arguments(uuid, note, null));
  }

  private static Stream<Arguments> test22() {
    // Uuid, appUser
    UUID uuid = UUID.randomUUID();
    return Stream.of(arguments(null, null), arguments(null, APP_USER), arguments(uuid, null));
  }

  private static Stream<Arguments> test23() {
    // Uuid, tagUuid, appUser
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    return Stream.of(
        arguments(null, null, null),
        arguments(null, null, APP_USER),
        arguments(null, tagUuid, null),
        arguments(null, tagUuid, APP_USER),
        arguments(uuid, null, null),
        arguments(uuid, null, APP_USER),
        arguments(uuid, tagUuid, null));
  }

  private static Stream<Arguments> test24() {
    // Uuid, tagUuid, appUser
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    return Stream.of(
        arguments(null, null, null),
        arguments(null, null, APP_USER),
        arguments(null, tagUuid, null),
        arguments(null, tagUuid, APP_USER),
        arguments(uuid, null, null),
        arguments(uuid, null, APP_USER),
        arguments(uuid, tagUuid, null));
  }

  private static Stream<Arguments> test25() {
    // Uuid, appUser
    UUID uuid = UUID.randomUUID();
    return Stream.of(arguments(null, null), arguments(null, APP_USER), arguments(uuid, null));
  }

  @BeforeEach
  void beforeEach() {
    noteService = new NoteServiceImpl(noteRepository, tagRepository, noteDtoMapper, tagDtoMapper);
  }

  @Test
  @DisplayName("Should create note and return note")
  void test1() {
    // Given
    NoteDto note = createNoteDto(null, TEXT);
    Note noteEntity = createNote(TEXT, Collections.emptySet(), null);
    Note savedNoteEntity = createNote(TEXT, Collections.emptySet(), APP_USER);
    NoteDto noteDto = createNoteDto(UUID.randomUUID(), TEXT);
    given(noteDtoMapper.convertToEntity(note)).willReturn(noteEntity);
    given(noteRepository.save(noteEntity)).willReturn(savedNoteEntity);
    given(noteDtoMapper.convertToDto(savedNoteEntity)).willReturn(noteDto);

    // When
    NoteDto res = noteService.createNote(note, APP_USER);

    // Then
    verify(noteDtoMapper, times(1)).convertToEntity(note);
    verify(noteRepository, times(1)).save(noteEntity);
    verify(noteDtoMapper, times(1)).convertToDto(savedNoteEntity);
    assertEquals(TEXT, res.getText());
    assertNotNull(res.getUuid());
  }

  @Test
  @DisplayName("Should not read note and return empty optional")
  void test2() {
    // Given
    Optional<Note> note = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(note);

    // When
    Optional<NoteDto> res = noteService.readNote(uuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(noteDtoMapper, times(0)).convertToDto((Note) any());
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName("Should read note and return note optional")
  void test3() {
    // Given
    Note note = createNote(TEXT, Collections.emptySet(), APP_USER);
    UUID uuid = UUID.randomUUID();
    NoteDto noteDto = createNoteDto(uuid, TEXT);
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(Optional.of(note));
    given(noteDtoMapper.convertToDto(note)).willReturn(noteDto);

    // When
    Optional<NoteDto> res = noteService.readNote(uuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(noteDtoMapper, times(1)).convertToDto(note);
    assertEquals(Optional.of(noteDto), res);
  }

  @Test
  @DisplayName("Should not read notes and return empty list")
  void test4() {
    // Given
    Page<Note> notes = Page.empty();
    given(noteRepository.findByAppUser(pageable, APP_USER)).willReturn(notes);

    // When
    Page<NoteDto> res = noteService.readNotes(pageable, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByAppUser(pageable, APP_USER);
    verify(noteDtoMapper, times(0)).convertToDto((Note) any());
    assertEquals(Page.empty(), res);
  }

  @Test
  @DisplayName("Should read notes and return note list")
  void test5() {
    // Given
    Note note = createNote(TEXT, Collections.emptySet(), APP_USER);
    NoteDto noteDto = createNoteDto(UUID.randomUUID(), TEXT);
    Page<Note> notes = new PageImpl<>(List.of(note));
    given(noteRepository.findByAppUser(pageable, APP_USER)).willReturn(notes);
    given(noteDtoMapper.convertToDto(note)).willReturn(noteDto);

    // When
    Page<NoteDto> res = noteService.readNotes(pageable, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByAppUser(pageable, APP_USER);
    verify(noteDtoMapper, times(1)).convertToDto(note);
    assertEquals(noteDto, res.get().findFirst().get());
  }

  @Test
  @DisplayName("Should not update note and return empty optional")
  void test6() {
    // Given
    Optional<Note> noteOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);

    // When
    Optional<NoteDto> res = noteService.updateNote(uuid, createNoteDto(uuid, TEXT), APP_USER);

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
    NoteDto updatedNoteDto = createNoteDto(uuid, OTHER_TEXT);
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(Optional.of(prevNote));
    given(noteRepository.save(prevNote)).willReturn(updatedNote);
    given(noteDtoMapper.convertToDto(updatedNote)).willReturn(updatedNoteDto);

    // When
    Optional<NoteDto> res = noteService.updateNote(uuid, updatedNoteDto, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(noteRepository, times(1)).save(prevNote);
    assertEquals(res, Optional.of(updatedNoteDto));
    assertEquals(OTHER_TEXT, res.get().getText());
  }

  @Test
  @DisplayName("Should not delete note and throw")
  void test8() {
    // Given
    Optional<Note> noteOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);

    // When
    DailyNotFoundException res =
        assertThrows(DailyNotFoundException.class, () -> noteService.deleteNote(uuid, APP_USER));

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(noteRepository, times(0)).delete(any());
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should delete note")
  void test9() {
    // Given
    Optional<Note> noteOptional = Optional.of(createNote(TEXT, Collections.emptySet(), APP_USER));
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);

    // When
    assertDoesNotThrow(() -> noteService.deleteNote(uuid, APP_USER));

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(noteRepository, times(1)).delete(noteOptional.get());
  }

  @Test
  @DisplayName("Should not add tag to note because of note not found")
  void test10() {
    // Given
    Optional<Note> noteOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class, () -> noteService.addTagToNote(uuid, tagUuid, APP_USER));

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(0)).findByUuidAndAppUser(any(), any());
    verify(noteRepository, times(0)).save(any());
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should not add tag to note because of tag not found")
  void test11() {
    // Given
    Optional<Note> noteOptional = Optional.of(createNote(TEXT, Collections.emptySet(), APP_USER));
    Optional<Tag> tagOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);
    given(tagRepository.findByUuidAndAppUser(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class, () -> noteService.addTagToNote(uuid, tagUuid, APP_USER));

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).findByUuidAndAppUser(tagUuid, APP_USER);
    verify(noteRepository, times(0)).save(any());
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should add tag to note")
  void test12() {
    // Given
    Note note = createNote(TEXT, new HashSet<>(), APP_USER);
    Optional<Note> noteOptional = Optional.of(note);
    Tag tag = createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER);
    Optional<Tag> tagOptional = Optional.of(tag);
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);
    given(tagRepository.findByUuidAndAppUser(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    assertDoesNotThrow(() -> noteService.addTagToNote(uuid, tagUuid, APP_USER));

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).findByUuidAndAppUser(tagUuid, APP_USER);
    verify(noteRepository, times(1)).save(note);
    assertTrue(note.getTags().contains(tag));
    assertTrue(tag.getNotes().contains(note));
  }

  @Test
  @DisplayName("Should not remove tag from note because of note not found")
  void test13() {
    // Given
    Optional<Note> noteOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class,
            () -> noteService.removeTagFromNote(uuid, tagUuid, APP_USER));

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(0)).findByUuidAndAppUser(any(), any());
    verify(noteRepository, times(0)).save(any());
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should not remove tag from note because of tag not found")
  void test14() {
    // Given
    Optional<Note> noteOptional = Optional.of(createNote(TEXT, Collections.emptySet(), APP_USER));
    Optional<Tag> tagOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);
    given(tagRepository.findByUuidAndAppUser(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    var res =
        assertThrows(
            DailyNotFoundException.class,
            () -> noteService.removeTagFromNote(uuid, tagUuid, APP_USER));

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).findByUuidAndAppUser(tagUuid, APP_USER);
    verify(noteRepository, times(0)).save(any());
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should remove tag from note")
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
    given(tagRepository.findByUuidAndAppUser(tagUuid, APP_USER)).willReturn(tagOptional);

    // When
    assertDoesNotThrow(() -> noteService.removeTagFromNote(uuid, tagUuid, APP_USER));

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).findByUuidAndAppUser(tagUuid, APP_USER);
    verify(noteRepository, times(1)).save(note);
    assertFalse(note.getTags().contains(tag));
    assertFalse(tag.getNotes().contains(note));
  }

  @Test
  @DisplayName("Should not read note tags and return empty optional")
  void test16() {
    // Given
    Optional<Note> noteOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUserFetchTags(uuid, APP_USER)).willReturn(noteOptional);

    // When
    Optional<Set<TagDto>> res = noteService.readNoteTags(uuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUserFetchTags(uuid, APP_USER);
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName("Should read note tags and return tag set optional")
  void test17() {
    // Given
    Set<Tag> tags = Set.of(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    TagDto tagDto = createTagDto(UUID.randomUUID(), NAME, COLOR_HEX);
    Optional<Note> noteOptional = Optional.of(createNote(TEXT, tags, APP_USER));
    UUID uuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUserFetchTags(uuid, APP_USER)).willReturn(noteOptional);
    given(tagDtoMapper.convertToDto(tags)).willReturn(Set.of(tagDto));

    // When
    Optional<Set<TagDto>> res = noteService.readNoteTags(uuid, APP_USER);

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUserFetchTags(uuid, APP_USER);
    assertEquals(Optional.of(Set.of(tagDto)), res);
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when create note with null argument")
  void test18(NoteDto note, String appUser) {
    assertThrows(IllegalArgumentException.class, () -> noteService.createNote(note, appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when read note with null argument")
  void test19(UUID uuid, String appUser) {
    assertThrows(IllegalArgumentException.class, () -> noteService.readNote(uuid, appUser));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when read notes with null argument")
  void test20(String appUser) {
    assertThrows(IllegalArgumentException.class, () -> noteService.readNotes(pageable, appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when update note with null argument")
  void test21(UUID uuid, NoteDto note, String appUser) {
    assertThrows(IllegalArgumentException.class, () -> noteService.updateNote(uuid, note, appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when delete note with null argument")
  void test22(UUID uuid, String appUser) {
    assertThrows(IllegalArgumentException.class, () -> noteService.deleteNote(uuid, appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when add tag to note with null argument")
  void test23(UUID uuid, UUID tagUuid, String appUser) {
    assertThrows(
        IllegalArgumentException.class, () -> noteService.addTagToNote(uuid, tagUuid, appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when remove tag from note with null argument")
  void test24(UUID uuid, UUID tagUuid, String appUser) {
    assertThrows(
        IllegalArgumentException.class,
        () -> noteService.removeTagFromNote(uuid, tagUuid, appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when read note tags with null argument")
  void test25(UUID uuid, String appUser) {
    assertThrows(IllegalArgumentException.class, () -> noteService.readNoteTags(uuid, appUser));
  }

  @Test
  @DisplayName("Should not add tag to note because of note tag limits and throw")
  void test26() {
    // Given
    Note note = createNote(TEXT, new HashSet<>(), APP_USER);
    Optional<Note> noteOptional = Optional.of(note);
    Optional<Tag> tagOptional = Optional.of(createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER));
    UUID uuid = UUID.randomUUID();
    UUID tagUuid = UUID.randomUUID();
    given(noteRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(noteOptional);
    given(tagRepository.findByUuidAndAppUser(tagUuid, APP_USER)).willReturn(tagOptional);
    createTag("name1", "#123456", new HashSet<>(), APP_USER).addToNote(note);
    createTag("name2", "#234567", new HashSet<>(), APP_USER).addToNote(note);
    createTag("name3", "#345678", new HashSet<>(), APP_USER).addToNote(note);
    createTag("name4", "#456789", new HashSet<>(), APP_USER).addToNote(note);
    createTag("name5", "#567890", new HashSet<>(), APP_USER).addToNote(note);

    // When
    DailyConflictException res =
        assertThrows(
            DailyConflictException.class, () -> noteService.addTagToNote(uuid, tagUuid, APP_USER));

    // Then
    verify(noteRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).findByUuidAndAppUser(tagUuid, APP_USER);
    verify(noteRepository, times(0)).save(any());
    assertEquals(Constants.ERROR_NOTE_TAGS_MAX, res.getMessage());
  }
}
