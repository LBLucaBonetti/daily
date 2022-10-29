package it.lbsoftware.daily.tags;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static it.lbsoftware.daily.notes.NoteTestUtils.createNote;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static it.lbsoftware.daily.tags.TagTestUtils.createTagDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.notes.Note;
import it.lbsoftware.daily.notes.NoteRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.web.server.ResponseStatusException;

@DisplayName("Tag integration tests")
class TagIntegrationTests extends DailyAbstractIntegrationTests {

  private static final String BASE_URL = "/api/tags";
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  private static final String APP_USER = "appUser";
  private static final String OTHER_APP_USER = "otherAppUser";
  private static final String OTHER_NAME = "otherName";
  private static final String OTHER_COLOR_HEX = "#654321";
  private static final String TEXT = "text";
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TagRepository tagRepository;
  @Autowired private TagDtoMapper tagDtoMapper;
  @Autowired private NoteRepository noteRepository;

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
  @ValueSource(
      strings = {
        "   ",
      })
  @DisplayName("Should return bad request when create tag with wrong name")
  void test9(final String name) throws Exception {
    // Given
    TagDto tagDto = createTagDto(null, name, COLOR_HEX);

    // When
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(APP_USER)))
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
    TagDto tagDto = createTagDto(null, NAME, colorHex);

    // When
    mockMvc
        .perform(
            post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isBadRequest());

    // Then
    assertEquals(0, tagRepository.count());
  }

  @Test
  @DisplayName("Should create tag")
  void test11() throws Exception {
    // Given
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
                        .with(loginOf(APP_USER)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TagDto.class);

    // Then
    assertNotNull(res.getUuid());
    assertEquals(NAME, res.getName());
    assertEquals(COLOR_HEX, res.getColorHex());
    Tag resEntity = tagRepository.findByUuidAndAppUser(res.getUuid(), APP_USER).get();
    assertNotNull(resEntity.getUuid());
    assertEquals(NAME, resEntity.getName());
    assertEquals(COLOR_HEX, resEntity.getColorHex());
  }

  @Test
  @DisplayName("Should return bad request when read tag with wrong uuid")
  void test12() throws Exception {
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
  @DisplayName("Should return not found when read tag of another app user")
  void test13() throws Exception {
    // Given
    UUID uuid =
        tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER)).getUuid();

    // When & then
    mockMvc
        .perform(
            get(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(loginOf(OTHER_APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when read tag and it does not exist")
  void test14() throws Exception {
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
  @DisplayName("Should read tag")
  void test15() throws Exception {
    // Given
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));

    // When
    TagDto res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL + "/{uuid}", tag.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(APP_USER)))
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
    tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    tagRepository.save(createTag(OTHER_NAME, OTHER_COLOR_HEX, Collections.emptySet(), APP_USER));

    // When
    PageDto<TagDto> res =
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
    List<TagDto> tagDtos = res.getContent();
    assertTrue(tagDtos.isEmpty());
  }

  @Test
  @DisplayName("Should read tags")
  void test17() throws Exception {
    // Given
    TagDto tagDto1 =
        tagDtoMapper.convertToDto(
            tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER)));
    TagDto tagDto2 =
        tagDtoMapper.convertToDto(
            tagRepository.save(
                createTag(OTHER_NAME, OTHER_COLOR_HEX, Collections.emptySet(), APP_USER)));

    // When
    PageDto<TagDto> res =
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
    List<TagDto> tagDtos = res.getContent();
    assertFalse(tagDtos.isEmpty());
    assertEquals(2, tagDtos.size());
    assertTrue(tagDtos.contains(tagDto1));
    assertTrue(tagDtos.contains(tagDto2));
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
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    TagDto tagDto = createTagDto(null, name, COLOR_HEX);

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(APP_USER)))
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
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    TagDto tagDto = createTagDto(null, NAME, colorHex);

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(APP_USER)))
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
  @DisplayName("Should return not found when update tag of another app user")
  void test21() throws Exception {
    // Given
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    TagDto tagDto = createTagDto(null, OTHER_NAME, OTHER_COLOR_HEX);

    // When & then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(OTHER_APP_USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should update tag")
  void test22() throws Exception {
    // Given
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    TagDto tagDto = createTagDto(null, OTHER_NAME, OTHER_COLOR_HEX);

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagDto))
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNoContent());

    // Then
    tag = tagRepository.findAll().get(0);
    assertEquals(OTHER_NAME, tag.getName());
    assertEquals(OTHER_COLOR_HEX, tag.getColorHex());
  }

  @Test
  @DisplayName("Should return bad request when delete tag with wrong uuid")
  void test23() throws Exception {
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
  @DisplayName("Should return not found when delete tag of another app user")
  void test24() throws Exception {
    // Given
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(OTHER_APP_USER)))
        .andExpect(status().isNotFound());

    // Then
    assertEquals(1, tagRepository.count());
  }

  @Test
  @DisplayName("Should return not found when delete tag and it does not exist")
  void test25() throws Exception {
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
  @DisplayName("Should not delete note and should remove tag from note tagSet when delete tag")
  void test26() throws Exception {
    // Given
    Note note = noteRepository.save(createNote(TEXT, new HashSet<>(), APP_USER));
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), APP_USER));
    tag.addToNote(note);
    noteRepository.save(note);
    assertTrue(tag.getNoteSet().contains(note));
    assertTrue(note.getTagSet().contains(tag));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(0, tagRepository.count());
    assertEquals(1, noteRepository.count());
    assertTrue(
        noteRepository
            .findByUuidAndAppUserFetchTags(note.getUuid(), APP_USER)
            .get()
            .getTagSet()
            .isEmpty());
  }

  @Test
  @DisplayName("Should delete tag")
  void test27() throws Exception {
    // Given
    Tag tag = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(APP_USER)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(0, tagRepository.count());
  }

  @Test
  @DisplayName("Should have id, createdAt, updatedAt and version when save tag")
  void test28() {
    // Given
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
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
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
    assertEquals(0, tag.getVersion());

    // When
    tagRepository.save(tag);
    Tag res = tagRepository.save(tag);

    // Then
    assertEquals(1, res.getVersion());
  }

  @Test
  @DisplayName("Should not equal when ids differ")
  void test30() {
    // Given
    Tag tag1 = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    Tag tag2 = tagRepository.save(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));

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
    String nonexistentField = "nonexistent-field";

    // When

    Exception res =
        mockMvc
            .perform(
                get(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("sort", nonexistentField)
                    .with(loginOf(APP_USER)))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResolvedException();

    // Then
    assertTrue(res instanceof ResponseStatusException);
    assertNull(((ResponseStatusException) res).getReason());
  }
}
