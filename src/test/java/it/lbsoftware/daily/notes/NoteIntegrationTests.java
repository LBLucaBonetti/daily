package it.lbsoftware.daily.notes;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static it.lbsoftware.daily.notes.NoteTestUtils.createNote;
import static it.lbsoftware.daily.notes.NoteTestUtils.createNoteDto;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import it.lbsoftware.daily.tags.TagRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("Note integration tests")
class NoteIntegrationTests extends DailyAbstractIntegrationTests {

  private static final String BASE_URL = "/api/notes";
  private static final String TEXT = "text";
  private static final String APP_USER = "appUser";
  private static final String OTHER_APP_USER = "otherAppUser";
  private static final String OTHER_TEXT = "otherText";
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  private static final String OTHER_NAME = "otherName";
  private static final String OTHER_COLOR_HEX = "#654321";
  @Autowired private ObjectMapper objectMapper;
  @Autowired private NoteRepository noteRepository;
  @Autowired private NoteDtoMapper noteDtoMapper;
  @Autowired private TagRepository tagRepository;
  @Autowired private TagDtoMapper tagDtoMapper;

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should return unauthorized when create note, csrf and no auth")
  void test1() throws Exception {
    mockMvc.perform(post(BASE_URL).with(csrf())).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when read note and no auth")
  void test2() throws Exception {
    mockMvc
        .perform(get(BASE_URL + "/{uuid}", UUID.randomUUID()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when read notes and no auth")
  void test3() throws Exception {
    mockMvc.perform(get(BASE_URL)).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when update note, csrf and no auth")
  void test4() throws Exception {
    mockMvc
        .perform(put(BASE_URL + "/{uuid}", UUID.randomUUID()).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when delete note, csrf and no auth")
  void test5() throws Exception {
    mockMvc
        .perform(delete(BASE_URL + "/{uuid}", UUID.randomUUID()).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when add tag to note, csrf and no auth")
  void test6() throws Exception {
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), UUID.randomUUID())
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when remove tag from note, csrf and no auth")
  void test7() throws Exception {
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), UUID.randomUUID())
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when read note tags and no auth")
  void test8() throws Exception {
    mockMvc
        .perform(get(BASE_URL + "/{uuid}/tags", UUID.randomUUID()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return forbidden when create note, no csrf and no auth")
  void test9() throws Exception {
    mockMvc.perform(post(BASE_URL)).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when update note, no csrf and no auth")
  void test10() throws Exception {
    mockMvc.perform(put(BASE_URL + "/{uuid}", UUID.randomUUID())).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when delete note, no csrf and no auth")
  void test11() throws Exception {
    mockMvc
        .perform(delete(BASE_URL + "/{uuid}", UUID.randomUUID()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when add tag to note, no csrf and no auth")
  void test12() throws Exception {
    mockMvc
        .perform(put(BASE_URL + "/{uuid}/tags/{tagsUuid}", UUID.randomUUID(), UUID.randomUUID()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when remove tag from note, no csrf and no auth")
  void test13() throws Exception {
    mockMvc
        .perform(delete(BASE_URL + "/{uuid}/tags/{tagsUuid}", UUID.randomUUID(), UUID.randomUUID()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(
      strings = {
        "   ",
      })
  @DisplayName("Should return bad request when create note with wrong text")
  void test14(final String text) throws Exception {
    // Given
    NoteDto noteDto = createNoteDto(null, text);

    // When
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDto))
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());

    // Then
    assertEquals(0, noteRepository.count());
  }

  @Test
  @DisplayName("Should create note")
  void test15() throws Exception {
    // Given
    NoteDto noteDto = createNoteDto(null, TEXT);

    // When
    NoteDto res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteDto))
                        .with(csrf())
                        .with(loginOf(APP_USER)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NoteDto.class);

    // Then
    assertNotNull(res.getUuid());
    assertEquals(TEXT, res.getText());
    Note resEntity = noteRepository.findByUuidAndAppUser(res.getUuid(), APP_USER).get();
    assertNotNull(resEntity.getUuid());
    assertEquals(TEXT, resEntity.getText());
  }

  @Test
  @DisplayName("Should return bad request when read note with wrong uuid")
  void test16() throws Exception {
    // Given
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when read note of another app user")
  void test17() throws Exception {
    // Given
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER)).getUuid();

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(OTHER_APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when read note and it does not exist")
  void test18() throws Exception {
    // Given
    UUID uuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should read note")
  void test19() throws Exception {
    // Given
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER));

    // When
    NoteDto res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/{uuid}", note.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(APP_USER)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NoteDto.class);

    // Then
    assertEquals(note.getUuid(), res.getUuid());
    assertEquals(note.getText(), res.getText());
  }

  @Test
  @DisplayName("Should return empty list when read notes of another app user")
  void test20() throws Exception {
    // Given
    noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER));
    noteRepository.save(createNote(OTHER_TEXT, Collections.emptySet(), APP_USER));

    // When
    List<NoteDto> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(OTHER_APP_USER)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    assertTrue(res.isEmpty());
  }

  @Test
  @DisplayName("Should read notes")
  void test21() throws Exception {
    // Given
    NoteDto noteDto1 =
        noteDtoMapper.convertToDto(
            noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER)));
    NoteDto noteDto2 =
        noteDtoMapper.convertToDto(
            noteRepository.save(createNote(OTHER_TEXT, Collections.emptySet(), APP_USER)));

    // When
    List<NoteDto> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL).contentType(MediaType.APPLICATION_JSON).with(loginOf(APP_USER)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    assertFalse(res.isEmpty());
    assertEquals(2, res.size());
    assertTrue(res.contains(noteDto1));
    assertTrue(res.contains(noteDto2));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(
      strings = {
        "   ",
      })
  @DisplayName("Should return bad request when update note with wrong text")
  void test22(final String text) throws Exception {
    // Given
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER));
    NoteDto noteDto = createNoteDto(null, text);

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDto))
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());

    // Then
    assertEquals(TEXT, noteRepository.findAll().get(0).getText());
  }

  @Test
  @DisplayName("Should return bad request when update note with wrong uuid")
  void test23() throws Exception {
    // Given
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when update note of another app user")
  void test24() throws Exception {
    // Given
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER));
    NoteDto noteDto = createNoteDto(null, OTHER_TEXT);

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDto))
                .with(csrf())
                .with(loginOf(OTHER_APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should update note")
  void test25() throws Exception {
    // Given
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER));
    NoteDto noteDto = createNoteDto(null, OTHER_TEXT);

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDto))
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(OTHER_TEXT, noteRepository.findAll().get(0).getText());
  }

  @Test
  @DisplayName("Should return bad request when delete note with wrong uuid")
  void test26() throws Exception {
    // Given
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when delete note of another app user")
  void test27() throws Exception {
    // Given
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(OTHER_APP_USER)))
        .andExpect(status().isNotFound());

    // Then
    assertEquals(1, noteRepository.count());
  }

  @Test
  @DisplayName("Should return not found when delete note and it does not exist")
  void test28() throws Exception {
    // Given
    UUID uuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should not delete tag and should remove note from tag noteSet when delete note")
  void test29() throws Exception {
    // Given
    Note note = noteRepository.save(createNote(TEXT, new HashSet<>(), APP_USER));
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER));
    tag.addToNote(note);
    noteRepository.save(note);
    assertTrue(note.getTagSet().contains(tag));
    assertTrue(tag.getNoteSet().contains(note));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(0, noteRepository.count());
    assertEquals(1, tagRepository.count());
    assertTrue(
        tagRepository
            .findByUuidAndAppUserFetchNotes(tag.getUuid(), APP_USER)
            .get()
            .getNoteSet()
            .isEmpty());
  }

  @Test
  @DisplayName("Should delete note")
  void test30() throws Exception {
    // Given
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(0, noteRepository.count());
  }

  @Test
  @DisplayName("Should return bad request when add tag to note with wrong uuid")
  void test31() throws Exception {
    // Given
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when add tag to note with wrong tagUuid")
  void test32() throws Exception {
    // Given
    String tagUuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when add tag to note with wrong uuid and tagUuid")
  void test33() throws Exception {
    // Given
    String uuid = "not-a-uuid";
    String tagUuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when add tag to note and note does not exist")
  void test34() throws Exception {
    // Given
    UUID uuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when add tag to note and tag does not exist")
  void test35() throws Exception {
    // Given
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER)).getUuid();
    UUID tagUuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when add tag to note and note is of another app user")
  void test36() throws Exception {
    // Given
    UUID uuid =
        noteRepository.save(createNote(TEXT, Collections.emptySet(), OTHER_APP_USER)).getUuid();

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when add tag to note and tag is of another app user")
  void test37() throws Exception {
    // Given
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER)).getUuid();
    UUID tagUuid =
        tagRepository
            .save(createTag(NAME, COLOR_HEX, Collections.emptySet(), OTHER_APP_USER))
            .getUuid();

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should add tag to note")
  void test38() throws Exception {
    // Given
    UUID uuid = noteRepository.save(createNote(TEXT, new HashSet<>(), APP_USER)).getUuid();
    UUID tagUuid =
        tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER)).getUuid();

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNoContent());

    // Then
    Note note = noteRepository.findByUuidAndAppUserFetchTags(uuid, APP_USER).get();
    Tag tag = tagRepository.findByUuidAndAppUserFetchNotes(tagUuid, APP_USER).get();
    assertEquals(1, note.getTagSet().size());
    assertTrue(note.getTagSet().contains(tag));
    assertEquals(1, tag.getNoteSet().size());
    assertTrue(tag.getNoteSet().contains(note));
  }

  @Test
  @DisplayName("Should return bad request when remove tag from note with wrong uuid")
  void test39() throws Exception {
    // Given
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when remove tag from note with wrong tagUuid")
  void test40() throws Exception {
    // Given
    String tagUuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when remove tag from note with wrong uuid and tagUuid")
  void test41() throws Exception {
    // Given
    String uuid = "not-a-uuid";
    String tagUuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when remove tag from note and note does not exist")
  void test42() throws Exception {
    // Given
    UUID uuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when remove tag from note and tag does not exist")
  void test43() throws Exception {
    // Given
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER)).getUuid();
    UUID tagUuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when remove tag from note and note is of another app user")
  void test44() throws Exception {
    // Given
    UUID uuid =
        noteRepository.save(createNote(TEXT, Collections.emptySet(), OTHER_APP_USER)).getUuid();

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when remove tag from note and tag is of another app user")
  void test45() throws Exception {
    // Given
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER)).getUuid();
    UUID tagUuid =
        tagRepository
            .save(createTag(NAME, COLOR_HEX, Collections.emptySet(), OTHER_APP_USER))
            .getUuid();

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should remove tag from note")
  void test46() throws Exception {
    // Given
    Note note = noteRepository.save(createNote(TEXT, new HashSet<>(), APP_USER));
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER));
    tag.addToNote(note);
    noteRepository.save(note);
    assertTrue(note.getTagSet().contains(tag));
    assertTrue(tag.getNoteSet().contains(note));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", note.getUuid(), tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNoContent());

    // Then
    note = noteRepository.findByUuidAndAppUserFetchTags(note.getUuid(), APP_USER).get();
    tag = tagRepository.findByUuidAndAppUserFetchNotes(tag.getUuid(), APP_USER).get();
    assertEquals(0, note.getTagSet().size());
    assertFalse(note.getTagSet().contains(tag));
    assertEquals(0, tag.getNoteSet().size());
    assertFalse(tag.getNoteSet().contains(note));
  }

  @Test
  @DisplayName("Should return bad request when read note tags with wrong uuid")
  void test47() throws Exception {
    // Given
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}/tags", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when read note tags and note does not exist")
  void test48() throws Exception {
    // Given
    UUID uuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}/tags", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when read note tags and note is of another app user")
  void test49() throws Exception {
    // Given
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), APP_USER)).getUuid();

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}/tags", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(OTHER_APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should read note tags")
  void test50() throws Exception {
    // Given
    Note note = noteRepository.save(createNote(TEXT, new HashSet<>(), APP_USER));
    Tag tag1 = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER));
    Tag tag2 =
        tagRepository.save(createTag(OTHER_NAME, OTHER_COLOR_HEX, new HashSet<>(), APP_USER));
    tag1.addToNote(note);
    tag2.addToNote(note);
    noteRepository.save(note);

    // When
    Set<TagDto> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/{uuid}/tags", note.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(APP_USER)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    assertEquals(2, res.size());
    assertTrue(res.contains(tagDtoMapper.convertToDto(tag1)));
    assertTrue(res.contains(tagDtoMapper.convertToDto(tag2)));
  }
}
