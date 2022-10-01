package it.lbsoftware.daily.tags;

import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

@DisplayName("TagServiceImpl unit tests")
class TagServiceImplTests extends DailyAbstractUnitTests {
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  private static final String APP_USER = "appUser";
  private static final String OTHER_NAME = "otherText";
  private static final String OTHER_COLOR_HEX = "#654321";
  @Mock private TagRepository tagRepository;
  private TagServiceImpl tagService;

  @BeforeEach
  void beforeEach() {
    tagService = new TagServiceImpl(tagRepository);
  }

  @Test
  @DisplayName("Should create tag and return tag")
  void test1() {
    // Given
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), null);
    Tag createdTag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
    given(tagRepository.save(tag)).willReturn(createdTag);

    // When
    Tag res = tagService.createTag(tag, APP_USER);

    // Then
    verify(tagRepository, times(1)).save(tag);
    assertEquals(APP_USER, res.getAppUser());
  }

  @Test
  @DisplayName("Should not read tag and return empty optional")
  void test2() {
    // Given
    Optional<Tag> tag = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(tagRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(tag);

    // When
    Optional<Tag> res = tagService.readTag(uuid, APP_USER);

    // Then
    verify(tagRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    assertEquals(tag, res);
  }

  @Test
  @DisplayName("Should read tag and return tag optional")
  void test3() {
    // Given
    Optional<Tag> tag = Optional.of(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    UUID uuid = UUID.randomUUID();
    given(tagRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(tag);

    // When
    Optional<Tag> res = tagService.readTag(uuid, APP_USER);

    // Then
    verify(tagRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    assertEquals(tag, res);
  }

  @Test
  @DisplayName("Should not read tags and return empty list")
  void test4() {
    // Given
    List<Tag> tags = Collections.emptyList();
    given(tagRepository.findByAppUser(APP_USER)).willReturn(tags);

    // When
    List<Tag> res = tagService.readTags(APP_USER);

    // Then
    verify(tagRepository, times(1)).findByAppUser(APP_USER);
    assertEquals(tags, res);
  }

  @Test
  @DisplayName("Should read tags and return tag list")
  void test5() {
    // Given
    List<Tag> tags = List.of(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    given(tagRepository.findByAppUser(APP_USER)).willReturn(tags);

    // When
    List<Tag> res = tagService.readTags(APP_USER);

    // Then
    verify(tagRepository, times(1)).findByAppUser(APP_USER);
    assertEquals(tags, res);
  }

  @Test
  @DisplayName("Should not update tag and return empty optional")
  void test6() {
    // Given
    Optional<Tag> tagOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(tagRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(tagOptional);

    // When
    Optional<Tag> res =
        tagService.updateTag(
            uuid, createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER), APP_USER);

    // Then
    verify(tagRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(0)).save(any());
    assertEquals(Optional.empty(), res);
  }

  @Test
  @DisplayName("Should update tag and return tag optional")
  void test7() {
    // Given
    Tag prevTag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
    Tag updatedTag = createTag(OTHER_NAME, OTHER_COLOR_HEX, Collections.emptySet(), APP_USER);
    UUID uuid = UUID.randomUUID();
    given(tagRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(Optional.of(prevTag));
    given(tagRepository.save(prevTag)).willReturn(updatedTag);

    // When
    Optional<Tag> res =
        tagService.updateTag(
            uuid, createTag(OTHER_NAME, OTHER_COLOR_HEX, Collections.emptySet(), null), APP_USER);

    // Then
    verify(tagRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).save(prevTag);
    assertEquals(res, Optional.of(updatedTag));
    assertEquals(OTHER_NAME, res.get().getName());
    assertEquals(OTHER_COLOR_HEX, res.get().getColorHex());
  }

  @Test
  @DisplayName("Should not delete tag and return false")
  void test8() {
    // Given
    Optional<Tag> tagOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(tagRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(tagOptional);

    // When
    Boolean res = tagService.deleteTag(uuid, APP_USER);

    // Then
    verify(tagRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(0)).delete(any());
    assertEquals(Boolean.FALSE, res);
  }

  @Test
  @DisplayName("Should delete tag and return true")
  void test9() {
    // Given
    Optional<Tag> tagOptional =
        Optional.of(createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER));
    UUID uuid = UUID.randomUUID();
    given(tagRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(tagOptional);

    // When
    Boolean res = tagService.deleteTag(uuid, APP_USER);

    // Then
    verify(tagRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).delete(tagOptional.get());
    assertEquals(Boolean.TRUE, res);
  }
}
