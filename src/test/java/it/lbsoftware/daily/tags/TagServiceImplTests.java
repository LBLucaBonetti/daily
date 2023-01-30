package it.lbsoftware.daily.tags;

import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static it.lbsoftware.daily.tags.TagTestUtils.createTagDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

@DisplayName("TagServiceImpl unit tests")
class TagServiceImplTests extends DailyAbstractUnitTests {
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  private static final String APP_USER = "appUser";
  private static final String OTHER_NAME = "otherText";
  private static final String OTHER_COLOR_HEX = "#654321";
  @Mock private TagRepository tagRepository;
  @Mock private Pageable pageable;
  @Mock private TagDtoMapper tagDtoMapper;
  private TagServiceImpl tagService;

  private static Stream<Arguments> test10() {
    // Tag, appUser
    TagDto tag = createTagDto(null, NAME, COLOR_HEX);
    return Stream.of(arguments(null, null), arguments(null, APP_USER), arguments(tag, null));
  }

  private static Stream<Arguments> test11() {
    // Uuid, appUser
    UUID uuid = UUID.randomUUID();
    return Stream.of(arguments(null, null), arguments(null, APP_USER), arguments(uuid, null));
  }

  private static Stream<Arguments> test13() {
    // Uuid, tag, appUser
    UUID uuid = UUID.randomUUID();
    TagDto tag = createTagDto(uuid, NAME, COLOR_HEX);
    return Stream.of(
        arguments(null, null, null),
        arguments(null, null, APP_USER),
        arguments(null, tag, null),
        arguments(null, tag, APP_USER),
        arguments(uuid, null, null),
        arguments(uuid, null, APP_USER),
        arguments(uuid, tag, null));
  }

  private static Stream<Arguments> test14() {
    // Uuid, appUser
    UUID uuid = UUID.randomUUID();
    return Stream.of(arguments(null, null), arguments(null, APP_USER), arguments(uuid, null));
  }

  @BeforeEach
  void beforeEach() {
    tagService = new TagServiceImpl(tagRepository, tagDtoMapper);
  }

  @Test
  @DisplayName("Should create tag and return tag")
  void test1() {
    // Given
    TagDto tag = createTagDto(null, NAME, COLOR_HEX);
    Tag tagEntity = createTag(NAME, COLOR_HEX, Collections.emptySet(), null);
    Tag savedTagEntity = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
    TagDto tagDto = createTagDto(UUID.randomUUID(), NAME, COLOR_HEX);
    given(tagDtoMapper.convertToEntity(tag)).willReturn(tagEntity);
    given(tagRepository.save(tagEntity)).willReturn(savedTagEntity);
    given(tagDtoMapper.convertToDto(savedTagEntity)).willReturn(tagDto);

    // When
    TagDto res = tagService.createTag(tag, APP_USER);

    // Then
    verify(tagDtoMapper, times(1)).convertToEntity(tag);
    verify(tagRepository, times(1)).save(tagEntity);
    verify(tagDtoMapper, times(1)).convertToDto(savedTagEntity);
    assertEquals(NAME, res.getName());
    assertEquals(COLOR_HEX, res.getColorHex());
    assertNotNull(res.getUuid());
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
    Page<Tag> tags = Page.empty();
    given(tagRepository.findByAppUser(pageable, APP_USER)).willReturn(tags);

    // When
    Page<TagDto> res = tagService.readTags(pageable, APP_USER);

    // Then
    verify(tagRepository, times(1)).findByAppUser(pageable, APP_USER);
    assertEquals(Page.empty(), res);
  }

  @Test
  @DisplayName("Should read tags and return tag list")
  void test5() {
    // Given
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
    TagDto tagDto = createTagDto(UUID.randomUUID(), NAME, COLOR_HEX);
    Page<Tag> tags = new PageImpl<>(List.of(tag));
    given(tagRepository.findByAppUser(pageable, APP_USER)).willReturn(tags);
    given(tagDtoMapper.convertToDto(tag)).willReturn(tagDto);

    // When
    Page<TagDto> res = tagService.readTags(pageable, APP_USER);

    // Then
    verify(tagRepository, times(1)).findByAppUser(pageable, APP_USER);
    verify(tagDtoMapper, times(1)).convertToDto(tag);
    assertEquals(tagDto, res.get().findFirst().get());
  }

  @Test
  @DisplayName("Should not update tag and return empty optional")
  void test6() {
    // Given
    Optional<Tag> tagOptional = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(tagRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(tagOptional);

    // When
    Optional<TagDto> res =
        tagService.updateTag(uuid, createTagDto(uuid, NAME, COLOR_HEX), APP_USER);

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
    TagDto updatedTagDto = createTagDto(uuid, OTHER_NAME, OTHER_COLOR_HEX);
    given(tagRepository.findByUuidAndAppUser(uuid, APP_USER)).willReturn(Optional.of(prevTag));
    given(tagRepository.save(prevTag)).willReturn(updatedTag);
    given(tagDtoMapper.convertToDto(updatedTag)).willReturn(updatedTagDto);

    // When
    Optional<TagDto> res = tagService.updateTag(uuid, updatedTagDto, APP_USER);

    // Then
    verify(tagRepository, times(1)).findByUuidAndAppUser(uuid, APP_USER);
    verify(tagRepository, times(1)).save(prevTag);
    assertEquals(res, Optional.of(updatedTagDto));
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

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when create tag with null argument")
  void test10(TagDto tag, String appUser) {
    assertThrows(IllegalArgumentException.class, () -> tagService.createTag(tag, appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when read tag with null argument")
  void test11(UUID uuid, String appUser) {
    assertThrows(IllegalArgumentException.class, () -> tagService.readTag(uuid, appUser));
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when read tags with null argument")
  void test12(String appUser) {
    assertThrows(IllegalArgumentException.class, () -> tagService.readTags(pageable, appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when update tag with null argument")
  void test13(UUID uuid, TagDto tag, String appUser) {
    assertThrows(IllegalArgumentException.class, () -> tagService.updateTag(uuid, tag, appUser));
  }

  @ParameterizedTest
  @MethodSource
  @DisplayName("Should throw when delete tag with null argument")
  void test14(UUID uuid, String appUser) {
    assertThrows(IllegalArgumentException.class, () -> tagService.deleteTag(uuid, appUser));
  }
}
