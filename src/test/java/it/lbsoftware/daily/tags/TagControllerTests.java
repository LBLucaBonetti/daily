package it.lbsoftware.daily.tags;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static it.lbsoftware.daily.tags.TagTestUtils.createTagDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
import it.lbsoftware.daily.exceptions.DailyNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class TagControllerTests extends DailyAbstractUnitTests {
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  private static final String EMAIL = "appuser@email.com";
  private static final UUID UNIQUE_ID = UUID.randomUUID();
  private static final AppUser APP_USER = createAppUser(UNIQUE_ID, EMAIL);
  @Mock private TagService tagService;
  @Mock private AppUserService appUserService;
  @Mock private OidcUser appUser;
  @Mock private Pageable pageable;
  private TagController tagController;

  @BeforeEach
  void beforeEach() {
    tagController = new TagController(tagService, appUserService);
    given(appUserService.getAppUser(appUser)).willReturn(APP_USER);
  }

  @Test
  @DisplayName("Should create tag and return created")
  void test1() {
    // Given
    TagDto tagDto = createTagDto(null, NAME, COLOR_HEX);
    TagDto createdTagDto = createTagDto(UUID.randomUUID(), NAME, COLOR_HEX);
    given(tagService.createTag(tagDto, APP_USER)).willReturn(createdTagDto);

    // When
    ResponseEntity<TagDto> res = tagController.createTag(tagDto, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(tagService, times(1)).createTag(tagDto, APP_USER);
    assertEquals(HttpStatus.CREATED, res.getStatusCode());
    assertEquals(createdTagDto, res.getBody());
  }

  @Test
  @DisplayName("Should not read tag and return not found")
  void test2() {
    // Given
    Optional<TagDto> readTag = Optional.empty();
    UUID uuid = UUID.randomUUID();
    given(tagService.readTag(uuid, APP_USER)).willReturn(readTag);

    // When
    ResponseEntity<TagDto> res = tagController.readTag(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(tagService, times(1)).readTag(uuid, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should read tag and return ok")
  void test3() {
    // Given
    UUID uuid = UUID.randomUUID();
    Optional<TagDto> readTag = Optional.of(createTagDto(uuid, NAME, COLOR_HEX));
    given(tagService.readTag(uuid, APP_USER)).willReturn(readTag);

    // When
    ResponseEntity<TagDto> res = tagController.readTag(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(tagService, times(1)).readTag(uuid, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(readTag.get(), res.getBody());
  }

  @Test
  @DisplayName("Should read tags and return ok")
  void test4() {
    // Given
    TagDto tagDto = createTagDto(UUID.randomUUID(), NAME, COLOR_HEX);
    Page<TagDto> readTags = new PageImpl<>(List.of(tagDto));
    given(tagService.readTags(pageable, APP_USER)).willReturn(readTags);

    // When
    ResponseEntity<PageDto<TagDto>> res = tagController.readTags(pageable, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(tagService, times(1)).readTags(pageable, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertNotNull(res.getBody());
    assertNotNull(res.getBody().getContent());
    assertEquals(readTags.getContent().size(), res.getBody().getContent().size());
    assertEquals(tagDto, res.getBody().getContent().get(0));
  }

  @Test
  @DisplayName("Should not update tag and return not found")
  void test5() {
    // Given
    UUID uuid = UUID.randomUUID();
    TagDto tagDto = createTagDto(uuid, NAME, COLOR_HEX);
    Optional<TagDto> updatedTagDto = Optional.empty();
    given(tagService.updateTag(uuid, tagDto, APP_USER)).willReturn(updatedTagDto);

    // When
    ResponseEntity<TagDto> res = tagController.updateTag(uuid, tagDto, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(tagService, times(1)).updateTag(uuid, tagDto, APP_USER);
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName("Should update tag and return ok")
  void test6() {
    // Given
    UUID uuid = UUID.randomUUID();
    TagDto tagDto = createTagDto(uuid, NAME, COLOR_HEX);
    Optional<TagDto> updatedTag = Optional.of(tagDto);
    given(tagService.updateTag(uuid, tagDto, APP_USER)).willReturn(updatedTag);

    // When
    ResponseEntity<TagDto> res = tagController.updateTag(uuid, tagDto, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(tagService, times(1)).updateTag(uuid, tagDto, APP_USER);
    assertEquals(HttpStatus.OK, res.getStatusCode());
    assertEquals(tagDto, res.getBody());
  }

  @Test
  @DisplayName("Should not delete tag and return not found")
  void test7() {
    // Given
    UUID uuid = UUID.randomUUID();
    doThrow(new DailyNotFoundException(Constants.ERROR_NOT_FOUND))
        .when(tagService)
        .deleteTag(uuid, APP_USER);

    // When
    DailyNotFoundException res =
        assertThrows(DailyNotFoundException.class, () -> tagController.deleteTag(uuid, appUser));

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(tagService, times(1)).deleteTag(uuid, APP_USER);
    assertEquals(Constants.ERROR_NOT_FOUND, res.getMessage());
  }

  @Test
  @DisplayName("Should delete tag and return no content")
  void test8() {
    // Given
    UUID uuid = UUID.randomUUID();
    doNothing().when(tagService).deleteTag(uuid, APP_USER);

    // When
    ResponseEntity<TagDto> res = tagController.deleteTag(uuid, appUser);

    // Then
    verify(appUserService, times(1)).getAppUser(appUser);
    verify(tagService, times(1)).deleteTag(uuid, APP_USER);
    assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    assertNull(res.getBody());
  }

  @Test
  @DisplayName(
      "Should not read tags because of wrong tag field name as sort parameter and return bad request")
  void test9() {
    // Given
    doThrow(new RuntimeException("Wrong field name as sort parameter"))
        .when(tagService)
        .readTags(pageable, APP_USER);

    // When
    DailyBadRequestException res =
        assertThrows(
            DailyBadRequestException.class, () -> tagController.readTags(pageable, appUser));

    // Then
    assertNotNull(res);
    verify(tagService, times(1)).readTags(pageable, APP_USER);
    assertNull(res.getMessage());
  }
}
