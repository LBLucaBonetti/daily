package it.lbsoftware.daily.tags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.appusers.WithMockAppUser;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test", "okta"})
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TagsIntegrationTests {

  private final String appUserUid = "123";
  private final String appUserEmail = "appUser@daily.email";
  @Autowired private TagServiceImpl tagService;
  @Autowired private TagController tagController;
  @Autowired private AppUserService appUserService;
  @Autowired private AppUserRepository appUserRepository;
  private AppUser au1;
  private TagDto t1dto;
  private TagDto t2dto;
  private TagDto t3dto;
  private Tag t1;

  @BeforeEach
  void setUp() {
    au1 = AppUser.builder().uid("234").email("anotherAppUser@daily.email").build();
    t1 = Tag.builder().name("Tag1").colorHex("#112233").build();
    t1dto = new TagDto();
    t1dto.setName("Tag1");
    t1dto.setColorHex("#112233");
    t2dto = new TagDto();
    t2dto.setName("Tag2");
    t2dto.setColorHex("#223344");
    t3dto = new TagDto();
    t3dto.setName("Tag1");
    t3dto.setColorHex("#223344");
  }

  @AfterEach
  void tearDown() {}

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldCreateTagAndFindItByUuid() {
    // given
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    // when
    t1dto = tagController.createTag(t1dto).getBody();
    // then
    TagDto resTagDto = tagController.readTag(t1dto.getUuid()).getBody();
    assertEquals(t1dto.getName(), resTagDto.getName());
    assertEquals(t1dto.getColorHex(), resTagDto.getColorHex());
    assertNotNull(resTagDto.getUuid());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldReadTags() {
    // given
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    tagController.createTag(t1dto);
    tagController.createTag(t2dto);
    // when
    ResponseEntity<List<TagDto>> res = tagController.readTags();
    // then
    assertEquals(2, res.getBody().size());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldNotReadAnotherAppUserTag() {
    // given
    au1 = appUserRepository.save(au1);
    UUID anotherAppUserTagUuid = tagService.createTag(t1, au1).getUuid();
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    // when
    ResponseEntity<TagDto> res = tagController.readTag(anotherAppUserTagUuid);
    // then
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldUpdateTagAndFindItByUuid() {
    // given
    AppUser appUser = appUserRepository.save(appUserService.getAppUserFromToken().get());
    t1 = tagService.createTag(t1, appUser);
    UUID tagUuid = t1.getUuid();
    // when
    tagController.updateTag(tagUuid, t3dto);
    // then
    TagDto resTagDto = tagController.readTag(tagUuid).getBody();
    assertEquals(t3dto.getName(), resTagDto.getName());
    assertEquals(t1.getUuid(), resTagDto.getUuid());
    assertNotEquals(t1.getColorHex(), resTagDto.getColorHex());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldNotUpdateAnotherAppUserTag() {
    // given
    au1 = appUserRepository.save(au1);
    UUID anotherAppUserTagUuid = tagService.createTag(t1, au1).getUuid();
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    // when
    ResponseEntity<TagDto> res = tagController.updateTag(anotherAppUserTagUuid, t3dto);
    // then
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldDeleteTagAndDoNotFindItByUuid() {
    // given
    AppUser appUser = appUserRepository.save(appUserService.getAppUserFromToken().get());
    UUID tagUuid = tagController.createTag(t1dto).getBody().getUuid();
    // when
    tagController.deleteTag(tagUuid);
    // then
    assertEquals(Optional.empty(), tagService.readTag(tagUuid, appUser));
    assertEquals(Collections.emptyList(), tagService.readTags(appUser));
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldNotDeleteAnotherAppUserTag() {
    // given
    au1 = appUserRepository.save(au1);
    UUID anotherAppUserTagUuid = tagService.createTag(t1, au1).getUuid();
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    // when
    ResponseEntity<TagDto> res = tagController.deleteTag(anotherAppUserTagUuid);
    // then
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }
}
