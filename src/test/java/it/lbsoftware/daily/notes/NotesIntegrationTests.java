package it.lbsoftware.daily.notes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.appusers.WithMockAppUser;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
class NotesIntegrationTests {

  private final String appUserUid = "123";
  private final String appUserEmail = "appUser@daily.email";
  @Autowired private NoteServiceImpl noteService;
  @Autowired private TagServiceImpl tagService;
  @Autowired private NoteController noteController;
  @Autowired private AppUserService appUserService;
  @Autowired private AppUserRepository appUserRepository;
  private AppUser au1;
  private NoteDto n1dto;
  private NoteDto n2dto;
  private NoteDto n3dto;
  private Note n1;
  private TagDto t1dto;
  private Tag t1;

  @BeforeEach
  void setUp() {
    au1 = AppUser.builder().uid("234").email("anotherAppUser@daily.email").build();
    n1dto = new NoteDto();
    n1dto.setText("Note1");
    n2dto = new NoteDto();
    n2dto.setText("Note2");
    n3dto = new NoteDto();
    n3dto.setText("UpdatedNote1");
    n1 = Note.builder().text("Note1").build();
    t1 = Tag.builder().name("Tag1").colorHex("#112233").build();
    t1dto = new TagDto();
    t1dto.setName("Tag1");
    t1dto.setColorHex("#112233");
  }

  @AfterEach
  void tearDown() {}

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldCreateNoteAndFindItByUuid() {
    // given
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    // when
    n1dto = noteController.createNote(n1dto).getBody();
    // then
    NoteDto resNoteDto = noteController.readNote(n1dto.getUuid()).getBody();
    assertEquals(n1dto.getText(), resNoteDto.getText());
    assertNotNull(resNoteDto.getUuid());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldReadNotes() {
    // given
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    noteController.createNote(n1dto);
    noteController.createNote(n2dto);
    // when
    ResponseEntity<List<NoteDto>> res = noteController.readNotes();
    // then
    assertEquals(2, res.getBody().size());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldNotReadAnotherAppUserNote() {
    // given
    au1 = appUserRepository.save(au1);
    UUID anotherAppUserNoteUuid = noteService.createNote(n1, au1).getUuid();
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    // when
    ResponseEntity<NoteDto> res = noteController.readNote(anotherAppUserNoteUuid);
    // then
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldUpdateNoteAndFindItByUuid() {
    // given
    AppUser appUser = appUserRepository.save(appUserService.getAppUserFromToken().get());
    n1 = noteService.createNote(n1, appUser);
    UUID noteUuid = n1.getUuid();
    // when
    noteController.updateNote(noteUuid, n3dto);
    // then
    NoteDto resNoteDto = noteController.readNote(noteUuid).getBody();
    assertEquals(n3dto.getText(), resNoteDto.getText());
    assertEquals(n1.getUuid(), resNoteDto.getUuid());
    assertNotEquals(n1.getText(), resNoteDto.getText());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldNotUpdateAnotherAppUserNote() {
    // given
    au1 = appUserRepository.save(au1);
    UUID anotherAppUserNoteUuid = noteService.createNote(n1, au1).getUuid();
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    // when
    ResponseEntity<NoteDto> res = noteController.updateNote(anotherAppUserNoteUuid, n3dto);
    // then
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldDeleteNoteAndDoNotFindItByUuid() {
    // given
    AppUser appUser = appUserRepository.save(appUserService.getAppUserFromToken().get());
    UUID noteUuid = noteController.createNote(n1dto).getBody().getUuid();
    // when
    noteController.deleteNote(noteUuid);
    // then
    assertEquals(Optional.empty(), noteService.readNote(noteUuid, appUser));
    assertEquals(Collections.emptyList(), noteService.readNotes(appUser));
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldNotDeleteAnotherAppUserNote() {
    // given
    au1 = appUserRepository.save(au1);
    UUID anotherAppUserNoteUuid = noteService.createNote(n1, au1).getUuid();
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    // when
    ResponseEntity<NoteDto> res = noteController.deleteNote(anotherAppUserNoteUuid);
    // then
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldAddTagToNoteAndFindIt() {
    // given
    AppUser appUser = appUserRepository.save(appUserService.getAppUserFromToken().get());
    n1 = noteService.createNote(n1, appUser);
    t1 = tagService.createTag(t1, appUser);
    noteController.addTagToNote(n1.getUuid(), t1.getUuid());
    // when
    ResponseEntity<Set<TagDto>> res = noteController.readNoteTags(n1.getUuid());
    // then
    assertEquals(1, res.getBody().size());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldNotAddAnotherAppUserTagToNote() {
    // given
    au1 = appUserRepository.save(au1);
    UUID anotherAppUserTagUuid = tagService.createTag(t1, au1).getUuid();
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    UUID noteUuid = noteController.createNote(n1dto).getBody().getUuid();
    // when
    ResponseEntity<TagDto> res = noteController.addTagToNote(noteUuid, anotherAppUserTagUuid);
    // then
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldNotAddTagToAnotherAppUserNote() {
    // given
    AppUser appUser = appUserRepository.save(appUserService.getAppUserFromToken().get());
    UUID tagUuid = tagService.createTag(t1, appUser).getUuid();
    au1 = appUserRepository.save(au1);
    UUID anotherAppUserNoteUuid = noteService.createNote(n1, au1).getUuid();
    // when
    ResponseEntity<TagDto> res = noteController.addTagToNote(anotherAppUserNoteUuid, tagUuid);
    // then
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldAddTagToNoteAndRemoveTagFromNote() {
    // given
    AppUser appUser = appUserRepository.save(appUserService.getAppUserFromToken().get());
    n1 = noteService.createNote(n1, appUser);
    t1 = tagService.createTag(t1, appUser);
    noteController.addTagToNote(n1.getUuid(), t1.getUuid());
    noteController.removeTagFromNote(n1.getUuid(), t1.getUuid());
    // when
    ResponseEntity<Set<TagDto>> res = noteController.readNoteTags(n1.getUuid());
    // then
    assertEquals(0, res.getBody().size());
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldReadNoteTags() {
    // given
    AppUser appUser = appUserRepository.save(appUserService.getAppUserFromToken().get());
    n1 = noteService.createNote(n1, appUser);
    t1 = tagService.createTag(t1, appUser);
    t1dto.setUuid(t1.getUuid());
    noteController.addTagToNote(n1.getUuid(), t1.getUuid());
    // when
    ResponseEntity<Set<TagDto>> res = noteController.readNoteTags(n1.getUuid());
    // then
    assertTrue(res.getBody().contains(t1dto));
  }

  @Test
  @WithMockAppUser(uid = appUserUid, email = appUserEmail, setAppUserAsDetails = true)
  void shouldNotReadAnotherAppUserNoteTags() {
    // given
    appUserRepository.save(appUserService.getAppUserFromToken().get());
    au1 = appUserRepository.save(au1);
    n1 = noteService.createNote(n1, au1);
    t1 = tagService.createTag(t1, au1);
    noteService.addTagToNote(n1.getUuid(), t1.getUuid(), au1);
    // when
    ResponseEntity<Set<TagDto>> res = noteController.readNoteTags(n1.getUuid());
    // then
    assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
  }
}
