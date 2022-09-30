package it.lbsoftware.daily.tags;

import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static it.lbsoftware.daily.tags.TagTestUtils.createTagDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUserService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@DisplayName("TagController unit tests")
class TagControllerTests extends DailyAbstractUnitTests {
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  private static final String APP_USER = "appUser";
  @Mock private TagService tagService;
  @Mock private TagDtoMapper tagDtoMapper;
  @Mock private AppUserService appUserService;
  @Mock private OidcUser appUser;
  private TagController tagController;

  @BeforeEach
  void beforeEach() {
    tagController = new TagController(tagService, tagDtoMapper, appUserService);
    given(appUserService.getUid(appUser)).willReturn(APP_USER);
  }

  @Test
  @DisplayName("Should create tag and return created")
  void test1() {
    // Given
    TagDto tagDto = createTagDto(null, NAME, COLOR_HEX);
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
    Tag createdTag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
    TagDto createdTagDto = createTagDto(UUID.randomUUID(), NAME, COLOR_HEX);
    given(tagDtoMapper.convertToEntity(tagDto)).willReturn(tag);
    given(tagService.createTag(tag, APP_USER)).willReturn(createdTag);
    given(tagDtoMapper.convertToDto(createdTag)).willReturn(createdTagDto);

    // When
    ResponseEntity<TagDto> res = tagController.createTag(tagDto, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(tagDtoMapper, times(1)).convertToEntity(tagDto);
    verify(tagService, times(1)).createTag(tag, APP_USER);
    verify(tagDtoMapper, times(1)).convertToDto(createdTag);
    assertEquals(HttpStatus.CREATED, res.getStatusCode());
    assertEquals(createdTagDto, res.getBody());
  }

  @Test
  @DisplayName("Should not read tag and return not found")
  void test2() {
    // Given
    Optional<Tag> readTag = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(tagService.readTag(uuid, APP_USER)).willReturn(readTag);

    // When
    ResponseEntity<TagDto> res = tagController.readTag(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(tagService, times(1)).readTag(uuid, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should read tag and return ok")
  void test3() {
    // Given
    Optional<Tag> readTag =
        Optional.of(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    UUID uuid = UUID.randomUUID();
    TagDto readTagDto = createTagDto(uuid, NAME, COLOR_HEX);
    given(tagService.readTag(uuid, APP_USER)).willReturn(readTag);
    given(tagDtoMapper.convertToDto(readTag.get())).willReturn(readTagDto);

    // When
    ResponseEntity<TagDto> res = tagController.readTag(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(tagService, times(1)).readTag(uuid, APP_USER);
    verify(tagDtoMapper, times(1)).convertToDto(readTag.get());
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(readTagDto, res.getBody());
  }

  @Test
  @DisplayName("Should read tags and return ok")
  void test4() {
    // Given
    List<Tag> readTags = List.of(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    List<TagDto> readTagDtos = List.of(createTagDto(UUID.randomUUID(), NAME, COLOR_HEX));
    given(tagService.readTags(APP_USER)).willReturn(readTags);
    given(tagDtoMapper.convertToDto(readTags)).willReturn(readTagDtos);

    // When
    ResponseEntity<List<TagDto>> res = tagController.readTags(appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(tagService, times(1)).readTags(APP_USER);
    verify(tagDtoMapper, times(1)).convertToDto(readTags);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(readTagDtos, res.getBody());
  }

  @Test
  @DisplayName("Should not update tag and return not found")
  void test5() {
    // Given
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
    Optional<Tag> updatedTag = Optional.empty();
    UUID uuid = UUID.randomUUID();
    TagDto tagDto = createTagDto(uuid, NAME, COLOR_HEX);
    given(tagDtoMapper.convertToEntity(tagDto)).willReturn(tag);
    given(tagService.updateTag(uuid, tag, APP_USER)).willReturn(updatedTag);

    // When
    ResponseEntity<TagDto> res = tagController.updateTag(uuid, tagDto, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(tagDtoMapper, times(1)).convertToEntity(tagDto);
    verify(tagService, times(1)).updateTag(uuid, tag, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should update tag and return no content")
  void test6() {
    // Given
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
    Optional<Tag> updatedTag = Optional.of(tag);
    UUID uuid = UUID.randomUUID();
    TagDto tagDto = createTagDto(uuid, NAME, COLOR_HEX);
    given(tagDtoMapper.convertToEntity(tagDto)).willReturn(tag);
    given(tagService.updateTag(uuid, tag, APP_USER)).willReturn(updatedTag);

    // When
    ResponseEntity<TagDto> res = tagController.updateTag(uuid, tagDto, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(tagDtoMapper, times(1)).convertToEntity(tagDto);
    verify(tagService, times(1)).updateTag(uuid, tag, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should not delete tag and return not found")
  void test7() {
    // Given
    UUID uuid = UUID.randomUUID();
    given(tagService.deleteTag(uuid, APP_USER)).willReturn(Boolean.FALSE);

    // When
    ResponseEntity<TagDto> res = tagController.deleteTag(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(tagService, times(1)).deleteTag(uuid, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should delete tag and return no content")
  void test8() {
    // Given
    UUID uuid = UUID.randomUUID();
    given(tagService.deleteTag(uuid, APP_USER)).willReturn(Boolean.TRUE);

    // When
    ResponseEntity<TagDto> res = tagController.deleteTag(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getUid(appUser);
    verify(tagService, times(1)).deleteTag(uuid, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }
}
