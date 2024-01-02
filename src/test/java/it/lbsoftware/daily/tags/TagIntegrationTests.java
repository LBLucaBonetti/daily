package it.lbsoftware.daily.tags;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_FULLNAME;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.OTHER_APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.OTHER_APP_USER_FULLNAME;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.saveOauth2OtherAppUser;
import static it.lbsoftware.daily.notes.NoteTestUtils.createNote;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static it.lbsoftware.daily.tags.TagTestUtils.createTagDto;
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
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
import it.lbsoftware.daily.notes.Note;
import it.lbsoftware.daily.notes.NoteRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

@DisplayName("Tag integration tests")
class TagIntegrationTests extends DailyAbstractIntegrationTests {

  private static final String BASE_URL = "/api/tags";
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  private static final String OTHER_NAME = "otherName";
  private static final String OTHER_COLOR_HEX = "#654321";
  private static final String TEXT = "text";
  private static final LocalDateTime A_LOCALDATETIME_IN_THE_PAST =
      LocalDateTime.of(1993, 5, 17, 0, 0, 0, 0);
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TagRepository tagRepository;
  @Autowired private TagDtoMapper tagDtoMapper;
  @Autowired private NoteRepository noteRepository;
  @Autowired private AppUserRepository appUserRepository;

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should return unauthorized when create tag, csrf and no auth")
  void test1() throws Exception {
    mockMvc.perform(post(BASE_URL).with(csrf())).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when read tag and no auth")
  void test2() throws Exception {
    mockMvc
        .perform(get(BASE_URL + "/{uuid}", UUID.randomUUID()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when read tags and no auth")
  void test3() throws Exception {
    mockMvc.perform(get(BASE_URL)).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when update tag, csrf and no auth")
  void test4() throws Exception {
    mockMvc
        .perform(put(BASE_URL + "/{uuid}", UUID.randomUUID()).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when delete tag, csrf and no auth")
  void test5() throws Exception {
    mockMvc
        .perform(delete(BASE_URL + "/{uuid}", UUID.randomUUID()).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return forbidden when create tag, no csrf and no auth")
  void test6() throws Exception {
    mockMvc.perform(post(BASE_URL)).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when update tag, no csrf and no auth")
  void test7() throws Exception {
    mockMvc.perform(put(BASE_URL + "/{uuid}", UUID.randomUUID())).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when delete tag, no csrf and no auth")
  void test8() throws Exception {
    mockMvc
        .perform(delete(BASE_URL + "/{uuid}", UUID.randomUUID()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "1234567890123456789012345678901"})
  @DisplayName("Should return bad request when create tag with wrong name")
  void test9(final String name) throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    TagDto tagDto = createTagDto(null, name, COLOR_HEX);

    // When
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());

    // Then
    assertEquals(0, tagRepository.count());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(
      strings = {
        "   ",
        "#GGGGGG",
        "#-1-1-1",
        "#@*?^!-",
        "#GGG",
        "#-1-",
        "#@*?",
        "#GGGGGGG",
        "#-1-1-1-",
        "#@*?^!-_",
        "#abcdeff",
        "#ABCDEFF",
        "#1234567",
        "AABBCC",
        "123456",
        "aabbcc",
        "gggggg",
        "aabbccc",
        "AABBCCC",
        "1234567",
      })
  @DisplayName("Should return bad request when create tag with wrong colorHex")
  void test10(final String colorHex) throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    TagDto tagDto = createTagDto(null, NAME, colorHex);

    // When
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());

    // Then
    assertEquals(0, tagRepository.count());
  }

  @Test
  @DisplayName("Should create tag")
  void test11() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    TagDto tagDto = createTagDto(null, NAME, COLOR_HEX);

    // When
    TagDto res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDto))
                        .with(csrf())
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TagDto.class);

    // Then
    assertNotNull(res.getUuid());
    assertEquals(NAME, res.getName());
    assertEquals(COLOR_HEX, res.getColorHex());
    Tag resEntity = tagRepository.findByUuidAndAppUser(res.getUuid(), appUser).get();
    assertNotNull(resEntity.getUuid());
    assertEquals(NAME, resEntity.getName());
    assertEquals(COLOR_HEX, resEntity.getColorHex());
  }

  @Test
  @DisplayName("Should return bad request when read tag with wrong uuid")
  void test12() throws Exception {
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
  @DisplayName("Should return not found when read tag of another app user")
  void test13() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    UUID uuid =
        tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser)).getUuid();

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(otherAppUser, OTHER_APP_USER_FULLNAME, OTHER_APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when read tag and it does not exist")
  void test14() throws Exception {
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
  @DisplayName("Should read tag")
  void test15() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));

    // When
    TagDto res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/{uuid}", tag.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TagDto.class);

    // Then
    assertEquals(tag.getUuid(), res.getUuid());
    assertEquals(tag.getName(), res.getName());
    assertEquals(tag.getColorHex(), res.getColorHex());
  }

  @Test
  @DisplayName("Should return empty list when read tags of another app user")
  void test16() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));
    tagRepository.save(createTag(OTHER_NAME, OTHER_COLOR_HEX, Collections.emptySet(), appUser));

    // When
    PageDto<TagDto> res =
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
    List<TagDto> tagDtos = res.getContent();
    assertTrue(tagDtos.isEmpty());
  }

  @Test
  @DisplayName("Should read tags")
  void test17() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    TagDto tagDto1 =
        tagDtoMapper.convertToDto(
            tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser)));
    TagDto tagDto2 =
        tagDtoMapper.convertToDto(
            tagRepository.save(
                createTag(OTHER_NAME, OTHER_COLOR_HEX, Collections.emptySet(), appUser)));

    // When
    PageDto<TagDto> res =
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
    List<TagDto> tagDtos = res.getContent();
    assertFalse(tagDtos.isEmpty());
    assertEquals(2, tagDtos.size());
    assertThat(tagDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(tagDto1);
    assertThat(tagDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(tagDto2);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(
      strings = {
        "   ",
      })
  @DisplayName("Should return bad request when update tag with wrong name")
  void test18(final String name) throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));
    TagDto tagDto = createTagDto(null, name, COLOR_HEX);

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());

    // Then
    tag = tagRepository.findAll().get(0);
    assertEquals(NAME, tag.getName());
    assertEquals(COLOR_HEX, tag.getColorHex());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(
      strings = {
        "   ",
        "#GGGGGG",
        "#-1-1-1",
        "#@*?^!-",
        "#GGG",
        "#-1-",
        "#@*?",
        "#GGGGGGG",
        "#-1-1-1-",
        "#@*?^!-_",
        "#abcdeff",
        "#ABCDEFF",
        "#1234567",
        "AABBCC",
        "123456",
        "aabbcc",
        "gggggg",
        "aabbccc",
        "AABBCCC",
        "1234567",
      })
  @DisplayName("Should return bad request when update tag with wrong colorHex")
  void test19(final String colorHex) throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));
    TagDto tagDto = createTagDto(null, NAME, colorHex);

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());

    // Then
    tag = tagRepository.findAll().get(0);
    assertEquals(NAME, tag.getName());
    assertEquals(COLOR_HEX, tag.getColorHex());
  }

  @Test
  @DisplayName("Should return bad request when update tag with wrong uuid")
  void test20() throws Exception {
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
  @DisplayName("Should return not found when update tag of another app user")
  void test21() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));
    TagDto tagDto = createTagDto(null, OTHER_NAME, OTHER_COLOR_HEX);

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(otherAppUser, OTHER_APP_USER_FULLNAME, OTHER_APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should update tag")
  void test22() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));
    TagDto tagDto = createTagDto(null, OTHER_NAME, OTHER_COLOR_HEX);

    // When
    TagDto res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    put(BASE_URL + "/{uuid}", tag.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDto))
                        .with(csrf())
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TagDto.class);

    // Then
    assertEquals(OTHER_NAME, res.getName());
    assertEquals(OTHER_COLOR_HEX, res.getColorHex());
  }

  @Test
  @DisplayName("Should return bad request when delete tag with wrong uuid")
  void test23() throws Exception {
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
  @DisplayName("Should return not found when delete tag of another app user")
  void test24() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    final UUID otherAppUser = saveOauth2OtherAppUser(appUserRepository);
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(otherAppUser, OTHER_APP_USER_FULLNAME, OTHER_APP_USER_EMAIL)))
        .andExpect(status().isNotFound());

    // Then
    assertEquals(1, tagRepository.count());
  }

  @Test
  @DisplayName("Should return not found when delete tag and it does not exist")
  void test25() throws Exception {
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
  @DisplayName("Should not delete note and should remove tag from note tags when delete tag")
  void test26() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Note note = noteRepository.save(createNote(TEXT, new HashSet<>(), appUser));
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), appUser));
    tag.addToNote(note);
    noteRepository.save(note);
    assertTrue(tag.getNotes().contains(note));
    assertTrue(note.getTags().contains(tag));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(0, tagRepository.count());
    assertEquals(1, noteRepository.count());
    assertTrue(
        noteRepository
            .findByUuidAndAppUserFetchTags(note.getUuid(), appUser)
            .get()
            .getTags()
            .isEmpty());
  }

  @Test
  @DisplayName("Should delete tag")
  void test27() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(0, tagRepository.count());
  }

  @Test
  @DisplayName("Should have id, createdAt, updatedAt and version when save tag")
  void test28() {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser);
    assertNull(tag.getCreatedAt());
    assertNull(tag.getUpdatedAt());
    assertEquals(0, tag.getVersion());

    // When
    Tag res = tagRepository.save(tag);

    // Then
    assertNotNull(res.getCreatedAt());
    assertNotNull(res.getUpdatedAt());
    assertEquals(0, res.getVersion());
  }

  @Test
  @DisplayName("Should update version when update tag")
  void test29() {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser);
    assertEquals(0, tag.getVersion());

    // When
    tagRepository.save(tag);
    tag.setName(tag.getName() + "1");
    Tag res = tagRepository.save(tag);

    // Then
    assertEquals(1, res.getVersion());
  }

  @Test
  @DisplayName("Should not equal when ids differ")
  void test30() {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Tag tag1 = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));
    Tag tag2 = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));

    // When
    boolean res = tag1.equals(tag2);

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName(
      "Should not read tags because of wrong tag field name as sort parameter and return bad request")
  void test31() throws Exception {
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
  @DisplayName("Should ignore uuid, createdAt and updatedAt from TagDto when create tag")
  void test32() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = UUID.randomUUID();
    LocalDateTime createdAt = A_LOCALDATETIME_IN_THE_PAST;
    LocalDateTime updatedAt = A_LOCALDATETIME_IN_THE_PAST;
    TagDto tagDto = createTagDto(uuid, NAME, COLOR_HEX);
    tagDto.setCreatedAt(createdAt);
    tagDto.setUpdatedAt(updatedAt);

    // When
    TagDto res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDto))
                        .with(csrf())
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TagDto.class);

    // Then
    assertNotNull(res.getUuid());
    assertNotNull(res.getCreatedAt());
    assertNotNull(res.getUpdatedAt());
    assertNotEquals(uuid, res.getUuid());
    assertNotEquals(createdAt, res.getCreatedAt());
    assertNotEquals(updatedAt, res.getUpdatedAt());
  }

  @Test
  @DisplayName("Should ignore uuid, createdAt and updatedAt from TagDto when update tag")
  void test33() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    UUID uuid = UUID.randomUUID();
    LocalDateTime createdAt = A_LOCALDATETIME_IN_THE_PAST;
    LocalDateTime updatedAt = A_LOCALDATETIME_IN_THE_PAST;
    TagDto tagDto = createTagDto(uuid, OTHER_NAME, OTHER_COLOR_HEX);
    tagDto.setCreatedAt(createdAt);
    tagDto.setUpdatedAt(updatedAt);

    // When
    String savedUuid =
        objectMapper
            .readValue(
                mockMvc
                    .perform(
                        post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                objectMapper.writeValueAsString(
                                    createTagDto(null, NAME, COLOR_HEX)))
                            .with(csrf())
                            .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString(),
                TagDto.class)
            .getUuid()
            .toString();
    TagDto updatedTag =
        objectMapper.readValue(
            mockMvc
                .perform(
                    put(BASE_URL + "/{uuid}", savedUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagDto))
                        .with(csrf())
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TagDto.class);

    // Then
    assertNotNull(updatedTag.getUuid());
    assertNotNull(updatedTag.getCreatedAt());
    assertNotNull(updatedTag.getUpdatedAt());
    assertNotEquals(uuid, updatedTag.getUuid());
    assertNotEquals(createdAt, updatedTag.getCreatedAt());
    assertNotEquals(updatedAt, updatedTag.getUpdatedAt());
  }

  @Test
  @DisplayName("Should cache when read tag")
  void test34() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));
    TagDto tagDto =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/{uuid}", tag.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TagDto.class);

    // When
    Optional<TagDto> res =
        Optional.ofNullable(cacheManager.getCache(Constants.TAG_CACHE))
            .map(r -> r.get("appUser:" + appUser + ":" + tag.getUuid().toString(), TagDto.class));

    // Then
    assertTrue(res.isPresent());
    assertEquals(tagDto, res.get());
  }

  @Test
  @DisplayName("Should cache when update tag")
  void test35() throws Exception {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), appUser));
    mockMvc.perform(
        put(BASE_URL + "/{uuid}", tag.getUuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(createTagDto(null, OTHER_NAME, OTHER_COLOR_HEX)))
            .with(csrf())
            .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)));
    TagDto tagDto =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/{uuid}", tag.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(appUser, APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TagDto.class);

    // When
    Optional<TagDto> res =
        Optional.ofNullable(cacheManager.getCache(Constants.TAG_CACHE))
            .map(r -> r.get("appUser:" + appUser + ":" + tag.getUuid().toString(), TagDto.class));

    // Then
    assertTrue(res.isPresent());
    assertEquals(tagDto, res.get());
  }

  @Test
  @DisplayName("Should not save tag when name size exceeds the limits")
  void test36() {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String aNameOf31Chars = """
        abcdefghijklmnopqrs
        abcdefghij
        """;

    // When
    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                tagRepository.save(
                    createTag(aNameOf31Chars, COLOR_HEX, Collections.emptySet(), appUser)));

    // Then
    assertNotNull(exception);
  }

  @Test
  @DisplayName("Should not save tag when colorHex size exceeds the limits")
  void test37() {
    // Given
    final UUID appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository);
    String aColorHexOf8Chars = "#1122334";

    // When
    Exception exception =
        assertThrows(
            Exception.class,
            () ->
                tagRepository.save(
                    createTag(NAME, aColorHexOf8Chars, Collections.emptySet(), appUser)));

    // Then
    assertNotNull(exception);
  }
}
