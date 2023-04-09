package it.lbsoftware.daily.notes;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_FULLNAME;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.OTHER_APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.OTHER_APP_USER_FULLNAME;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.saveOauth2OtherAppUser;
import static it.lbsoftware.daily.notes.NoteTestUtils.createNote;
import static it.lbsoftware.daily.notes.NoteTestUtils.createNoteDto;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserTestUtils;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exception.DailyBadRequestException;
import it.lbsoftware.daily.exception.DailyConflictException;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import it.lbsoftware.daily.tags.TagRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
  private static final String OTHER_TEXT = "otherText";
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  private static final String OTHER_NAME = "otherName";
  private static final String OTHER_COLOR_HEX = "#654321";
  private static final LocalDateTime A_LOCALDATETIME_IN_THE_PAST =
      LocalDateTime.of(1993, 5, 17, 0, 0, 0, 0);
  @Autowired private ObjectMapper objectMapper;
  @Autowired private NoteRepository noteRepository;
  @Autowired private NoteDtoMapper noteDtoMapper;
  @Autowired private TagRepository tagRepository;
  @Autowired private TagDtoMapper tagDtoMapper;
  @Autowired private AppUserRepository appUserRepository;

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
        """
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmno
        """
      })
  @DisplayName("Should return bad request when create note with wrong text")
  void test14(final String text) throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    NoteDto noteDto = createNoteDto(null, text);

    // When
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDto))
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());

    // Then
    assertEquals(0, noteRepository.count());
  }

  @Test
  @DisplayName("Should create note")
  void test15() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
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
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NoteDto.class);

    // Then
    assertNotNull(res.getUuid());
    assertEquals(TEXT, res.getText());
    Note resEntity = noteRepository.findByUuidAndAppUser(res.getUuid(), appUser).get();
    assertNotNull(resEntity.getUuid());
    assertEquals(TEXT, resEntity.getText());
  }

  @Test
  @DisplayName("Should return bad request when read note with wrong uuid")
  void test16() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when read note of another app user")
  void test17() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser)).getUuid();

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(otherAppUser, OTHER_APP_USER_FULLNAME, OTHER_APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when read note and it does not exist")
  void test18() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should read note")
  void test19() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser));

    // When
    NoteDto res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/{uuid}", note.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
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
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser));
    noteRepository.save(createNote(OTHER_TEXT, Collections.emptySet(), appUser));

    // When
    PageDto<NoteDto> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(otherAppUser, OTHER_APP_USER_FULLNAME, OTHER_APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    List<NoteDto> noteDtos = res.getContent();
    assertTrue(noteDtos.isEmpty());
  }

  @Test
  @DisplayName("Should read notes")
  void test21() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    NoteDto noteDto1 =
        noteDtoMapper.convertToDto(
            noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser)));
    NoteDto noteDto2 =
        noteDtoMapper.convertToDto(
            noteRepository.save(createNote(OTHER_TEXT, Collections.emptySet(), appUser)));

    // When
    PageDto<NoteDto> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    List<NoteDto> noteDtos = res.getContent();
    assertFalse(noteDtos.isEmpty());
    assertEquals(2, noteDtos.size());
    assertThat(noteDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(noteDto1);
    assertThat(noteDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(noteDto2);
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
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser));
    NoteDto noteDto = createNoteDto(null, text);

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDto))
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());

    // Then
    note = noteRepository.findAll().get(0);
    assertEquals(TEXT, note.getText());
  }

  @Test
  @DisplayName("Should return bad request when update note with wrong uuid")
  void test23() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when update note of another app user")
  void test24() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser));
    NoteDto noteDto = createNoteDto(null, OTHER_TEXT);

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDto))
                .with(csrf())
                .with(loginOf(otherAppUser, OTHER_APP_USER_FULLNAME, OTHER_APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should update note")
  void test25() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser));
    NoteDto noteDto = createNoteDto(null, OTHER_TEXT);

    // When
    NoteDto res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    put(BASE_URL + "/{uuid}", note.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteDto))
                        .with(csrf())
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NoteDto.class);

    // Then
    assertEquals(OTHER_TEXT, res.getText());
  }

  @Test
  @DisplayName("Should return bad request when delete note with wrong uuid")
  void test26() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when delete note of another app user")
  void test27() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(otherAppUser, OTHER_APP_USER_FULLNAME, OTHER_APP_USER_EMAIL)))
        .andExpect(status().isNotFound());

    // Then
    assertEquals(1, noteRepository.count());
  }

  @Test
  @DisplayName("Should return not found when delete note and it does not exist")
  void test28() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should not delete tag and should remove note from tag notes when delete note")
  void test29() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, new HashSet<>(), appUser));
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), appUser));
    tag.addToNote(note);
    noteRepository.save(note);
    assertTrue(note.getTags().contains(tag));
    assertTrue(tag.getNotes().contains(note));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(0, noteRepository.count());
    assertEquals(1, tagRepository.count());
    assertTrue(
        tagRepository
            .findByUuidAndAppUserFetchNotes(tag.getUuid(), appUser)
            .get()
            .getNotes()
            .isEmpty());
  }

  @Test
  @DisplayName("Should delete note")
  void test30() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", note.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(0, noteRepository.count());
  }

  @Test
  @DisplayName("Should return bad request when add tag to note with wrong uuid")
  void test31() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when add tag to note with wrong tagUuid")
  void test32() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String tagUuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when add tag to note with wrong uuid and tagUuid")
  void test33() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String uuid = "not-a-uuid";
    String tagUuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when add tag to note and note does not exist")
  void test34() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when add tag to note and tag does not exist")
  void test35() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser)).getUuid();
    UUID tagUuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when add tag to note and note is of another app user")
  void test36() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    UUID uuid =
        noteRepository.save(createNote(TEXT, Collections.emptySet(), otherAppUser)).getUuid();

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when add tag to note and tag is of another app user")
  void test37() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser)).getUuid();
    UUID tagUuid =
        tagRepository
            .save(createTag(NAME, COLOR_HEX, Collections.emptySet(), otherAppUser))
            .getUuid();

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should add tag to note")
  void test38() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = noteRepository.save(createNote(TEXT, new HashSet<>(), appUser)).getUuid();
    UUID tagUuid =
        tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), appUser)).getUuid();

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNoContent());

    // Then
    Note note = noteRepository.findByUuidAndAppUserFetchTags(uuid, appUser).get();
    Tag tag = tagRepository.findByUuidAndAppUserFetchNotes(tagUuid, appUser).get();
    assertEquals(1, note.getTags().size());
    assertTrue(note.getTags().contains(tag));
    assertEquals(1, tag.getNotes().size());
    assertTrue(tag.getNotes().contains(note));
  }

  @Test
  @DisplayName("Should return bad request when remove tag from note with wrong uuid")
  void test39() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when remove tag from note with wrong tagUuid")
  void test40() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String tagUuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when remove tag from note with wrong uuid and tagUuid")
  void test41() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String uuid = "not-a-uuid";
    String tagUuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when remove tag from note and note does not exist")
  void test42() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when remove tag from note and tag does not exist")
  void test43() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser)).getUuid();
    UUID tagUuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when remove tag from note and note is of another app user")
  void test44() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    UUID uuid =
        noteRepository.save(createNote(TEXT, Collections.emptySet(), otherAppUser)).getUuid();

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when remove tag from note and tag is of another app user")
  void test45() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser)).getUuid();
    UUID tagUuid =
        tagRepository
            .save(createTag(NAME, COLOR_HEX, Collections.emptySet(), otherAppUser))
            .getUuid();

    // When & then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should remove tag from note")
  void test46() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, new HashSet<>(), appUser));
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), appUser));
    tag.addToNote(note);
    noteRepository.save(note);
    assertTrue(note.getTags().contains(tag));
    assertTrue(tag.getNotes().contains(note));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", note.getUuid(), tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNoContent());

    // Then
    note = noteRepository.findByUuidAndAppUserFetchTags(note.getUuid(), appUser).get();
    tag = tagRepository.findByUuidAndAppUserFetchNotes(tag.getUuid(), appUser).get();
    assertEquals(0, note.getTags().size());
    assertFalse(note.getTags().contains(tag));
    assertEquals(0, tag.getNotes().size());
    assertFalse(tag.getNotes().contains(note));
  }

  @Test
  @DisplayName("Should return bad request when read note tags with wrong uuid")
  void test47() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String uuid = "not-a-uuid";

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}/tags", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when read note tags and note does not exist")
  void test48() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = UUID.randomUUID();

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}/tags", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when read note tags and note is of another app user")
  void test49() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    UUID uuid = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser)).getUuid();

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}/tags", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(otherAppUser, OTHER_APP_USER_FULLNAME, OTHER_APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should read note tags")
  void test50() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, new HashSet<>(), appUser));
    Tag tag1 = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), appUser));
    Tag tag2 = tagRepository.save(createTag(OTHER_NAME, OTHER_COLOR_HEX, new HashSet<>(), appUser));
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
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    assertEquals(2, res.size());
    assertThat(res)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(tagDtoMapper.convertToDto(tag1));
    assertThat(res)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(tagDtoMapper.convertToDto(tag2));
  }

  @Test
  @DisplayName("Should have id, createdAt, updatedAt and version when save note")
  void test51() {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = createNote(TEXT, Collections.emptySet(), appUser);
    assertNull(note.getCreatedAt());
    assertNull(note.getUpdatedAt());
    assertEquals(0, note.getVersion());

    // When
    Note res = noteRepository.save(note);

    // Then
    assertNotNull(res.getCreatedAt());
    assertNotNull(res.getUpdatedAt());
    assertEquals(0, res.getVersion());
  }

  @Test
  @DisplayName("Should update version when update note")
  void test52() {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = createNote(TEXT, Collections.emptySet(), appUser);
    assertEquals(0, note.getVersion());

    // When
    noteRepository.save(note);
    Note res = noteRepository.save(note);

    // Then
    assertEquals(1, res.getVersion());
  }

  @Test
  @DisplayName("Should not equal when ids differ")
  void test53() {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note1 = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser));
    Note note2 = noteRepository.save(createNote(TEXT, Collections.emptySet(), appUser));

    // When
    boolean res = note1.equals(note2);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should not save note when text size exceeds the limits")
  void test54() {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String aTextOf256Chars =
        """
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmnopqrs
        abcdefghijklmno
        """;

    // When
    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                noteRepository.save(createNote(aTextOf256Chars, Collections.emptySet(), appUser)));

    // Then
    assertNotNull(exception);
  }

  @Test
  @DisplayName(
      "Should not read notes because of wrong note field name as sort parameter and return bad request")
  void test55() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String nonexistentField = "nonexistent-field";

    // When
    var res =
        mockMvc
            .perform(
                get(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("sort", nonexistentField)
                    .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResolvedException();

    // Then
    assertTrue(res instanceof DailyBadRequestException);
    assertNull(res.getMessage());
  }

  @Test
  @DisplayName("Should not add tag to note because of note tag limits and return conflict")
  void test56() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, new HashSet<>(), appUser));
    UUID uuid = note.getUuid();
    UUID tagUuid =
        tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), appUser)).getUuid();
    // Reach the max number of tags for this note
    for (Tag tag :
        List.of(
            tagRepository.save(createTag("name1", "#123456", new HashSet<>(), appUser)),
            tagRepository.save(createTag("name2", "#234567", new HashSet<>(), appUser)),
            tagRepository.save(createTag("name3", "#345678", new HashSet<>(), appUser)),
            tagRepository.save(createTag("name4", "#456789", new HashSet<>(), appUser)),
            tagRepository.save(createTag("name5", "#567890", new HashSet<>(), appUser)))) {
      mockMvc.perform(
          put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tag.getUuid())
              .contentType(MediaType.APPLICATION_JSON)
              .with(csrf())
              .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)));
    }

    // When
    var res =
        mockMvc
            .perform(
                put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
            .andExpect(status().isConflict())
            .andReturn()
            .getResolvedException();

    // Then
    assertTrue(res instanceof DailyConflictException);
    assertEquals(Constants.ERROR_NOTE_TAGS_MAX, res.getMessage());
  }

  @Test
  @DisplayName("Should ignore uuid, createdAt and updatedAt from NoteDto when create note")
  void test57() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = UUID.randomUUID();
    LocalDateTime createdAt = A_LOCALDATETIME_IN_THE_PAST;
    LocalDateTime updatedAt = A_LOCALDATETIME_IN_THE_PAST;
    NoteDto noteDto = createNoteDto(uuid, TEXT);
    noteDto.setCreatedAt(createdAt);
    noteDto.setUpdatedAt(updatedAt);

    // When
    NoteDto res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteDto))
                        .with(csrf())
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NoteDto.class);

    // Then
    assertNotNull(res.getUuid());
    assertNotNull(res.getCreatedAt());
    assertNotNull(res.getUpdatedAt());
    assertNotEquals(uuid, res.getUuid());
    assertNotEquals(createdAt, res.getCreatedAt());
    assertNotEquals(updatedAt, res.getUpdatedAt());
  }

  @Test
  @DisplayName("Should ignore uuid, createdAt and updatedAt from NoteDto when update note")
  void test58() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = UUID.randomUUID();
    LocalDateTime createdAt = A_LOCALDATETIME_IN_THE_PAST;
    LocalDateTime updatedAt = A_LOCALDATETIME_IN_THE_PAST;
    NoteDto noteDto = createNoteDto(uuid, OTHER_TEXT);
    noteDto.setCreatedAt(createdAt);
    noteDto.setUpdatedAt(updatedAt);

    // When
    String savedUuid =
        objectMapper
            .readValue(
                mockMvc
                    .perform(
                        post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createNoteDto(null, TEXT)))
                            .with(csrf())
                            .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString(),
                NoteDto.class)
            .getUuid()
            .toString();
    NoteDto updatedNote =
        objectMapper.readValue(
            mockMvc
                .perform(
                    put(BASE_URL + "/{uuid}", savedUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteDto))
                        .with(csrf())
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NoteDto.class);

    // Then
    assertNotNull(updatedNote.getUuid());
    assertNotNull(updatedNote.getCreatedAt());
    assertNotNull(updatedNote.getUpdatedAt());
    assertNotEquals(uuid, updatedNote.getUuid());
    assertNotEquals(createdAt, updatedNote.getCreatedAt());
    assertNotEquals(updatedAt, updatedNote.getUpdatedAt());
  }

  @Test
  @DisplayName("Should cache when read note tags")
  void test59() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, new HashSet<>(), appUser));
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), appUser));
    tag.addToNote(note);
    noteRepository.save(note);
    Set<TagDto> tagDtos =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/{uuid}/tags", note.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});
    TagDto tagDto = tagDtos.stream().findFirst().get();

    // When
    Optional<Set<TagDto>> res =
        Optional.ofNullable(cacheManager.getCache(Constants.NOTE_CACHE))
            .map(
                r ->
                    r.get(
                        "appUser:" + appUser + ":" + note.getUuid().toString() + ":tags",
                        Set.class));

    // Then
    assertTrue(res.isPresent());
    assertTrue(res.get().contains(tagDto));
  }
}
