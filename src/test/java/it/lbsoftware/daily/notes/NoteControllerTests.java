package it.lbsoftware.daily.notes;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ActiveProfiles(profiles = {"test", "okta"})
@WebMvcTest(NoteController.class)
class NoteControllerTests {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean private AppUserService appUserService;
  @MockBean private NoteService noteService;
  @MockBean private NoteDtoMapper noteDtoMapper;
  @MockBean private TagDtoMapper tagDtoMapper;
  private Note n1;
  private Note n2;
  private Note n3;
  private Note n4;
  private Note n5;
  private NoteDto n1dto;
  private NoteDto n2dto;
  private NoteDto n3dto;
  private NoteDto n4dto;
  private NoteDto n5dto;
  private Tag t1;
  private Tag t2;
  private TagDto t1dto;
  private TagDto t2dto;
  private AppUser au1;
  private AppUser au2;
  private UUID uuid1;
  private UUID uuid2;

  @BeforeEach
  void setUp() {
    uuid1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    uuid2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    n1 = Note.builder().text("Note1").build();
    n2 = Note.builder().text("Note2").build();
    n1dto = new NoteDto();
    n1dto.setText(n1.getText());
    n1dto.setUuid(uuid1);
    n2dto = new NoteDto();
    n2dto.setText(n2.getText());
    n2dto.setUuid(uuid2);
    t1 = Tag.builder().name("Tag1").colorHex("#112233").build();
    t2 = Tag.builder().name("Tag2").colorHex("#223344").build();
    t1dto = new TagDto();
    t1dto.setName(t1.getName());
    t1dto.setColorHex(t1.getColorHex());
    t2dto = new TagDto();
    t2dto.setName(t2.getName());
    t2dto.setColorHex(t2.getColorHex());
    au1 = AppUser.builder().uid("123").email("au1@daily.it").build();
    au2 = AppUser.builder().uid("234").email("au2@daily.it").build();
    n3dto = new NoteDto();
    n3dto.setText(n1.getText());
    n4dto = new NoteDto();
    n4dto.setText("");
    n3 = Note.builder().text("").build();
    n4 = Note.builder().text("Note1Updated").build();
    n5dto = new NoteDto();
    n5dto.setText(n4.getText());
    n5dto.setUuid(uuid1);
    n5 = Note.builder().text("Note1Updated").appUser(au1).build();
  }

  @AfterEach
  void tearDown() {}

  @Test
  void givenNoAuth_whenReadNotes_thenUnauthorized() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/notes").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void givenNoNotes_whenReadNotes_thenOk() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.readNotes(au1)).willReturn(Collections.emptyList());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/notes").with(jwt()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("[]"));
  }

  @Test
  void givenTwoNotes_whenReadNotes_thenOk() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.readNotes(au1)).willReturn(List.of(n1, n2));
    given(noteDtoMapper.convertToDto(n1)).willReturn(n1dto);
    given(noteDtoMapper.convertToDto(n2)).willReturn(n2dto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/notes").with(jwt()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[*].text", containsInAnyOrder(n1dto.getText(), n2dto.getText())))
        .andExpect(
            jsonPath(
                "$.[*].uuid",
                containsInAnyOrder(n1dto.getUuid().toString(), n2dto.getUuid().toString())));
  }

  @Test
  void givenNoAppUser_whenReadNotes_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/notes").with(jwt()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenNote_whenCreateNote_thenCreated() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteDtoMapper.convertToEntity(n3dto)).willReturn(n1);
    given(noteService.createNote(n1, au1)).willReturn(n2);
    given(noteDtoMapper.convertToDto(n2)).willReturn(n1dto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes")
                .with(jwt())
                .content(objectMapper.writeValueAsString(n3dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.text").value(n1dto.getText()))
        .andExpect(jsonPath("$.uuid").value(n1dto.getUuid().toString()));
  }

  @Test
  void givenWrongTextNote_whenCreateNote_thenBadRequest() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes")
                .with(jwt())
                .content(objectMapper.writeValueAsString(n4dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void givenNoAppUser_whenCreateNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/notes")
                .with(jwt())
                .content(objectMapper.writeValueAsString(n3dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenNote_whenReadNote_thenOk() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.readNote(uuid1, au1)).willReturn(Optional.of(n1));
    given(noteDtoMapper.convertToDto(n1)).willReturn(n1dto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/notes/{uuid}", uuid1)
                .with(jwt())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.text").value(n1dto.getText()))
        .andExpect(jsonPath("$.uuid").value(n1dto.getUuid().toString()));
  }

  @Test
  void givenNoNotes_whenReadNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.readNote(uuid1, au1)).willReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/notes/{uuid}", uuid1)
                .with(jwt())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenNoAppUser_whenReadNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/notes/{uuid}", uuid1)
                .with(jwt())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenNote_whenUpdateNote_thenNoContent() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteDtoMapper.convertToEntity(n5dto)).willReturn(n4);
    given(noteService.updateNote(uuid1, n4, au1)).willReturn(Optional.of(n5));
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/notes/{uuid}", uuid1)
                .with(jwt())
                .content(objectMapper.writeValueAsString(n5dto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void givenWrongTextNote_whenUpdateNote_thenBadRequest() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/notes/{uuid}", uuid1)
                .with(jwt())
                .content(objectMapper.writeValueAsString(n4dto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void givenNoNotes_whenUpdateNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteDtoMapper.convertToEntity(n3dto)).willReturn(n3);
    given(noteService.updateNote(uuid1, n3, au1)).willReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/notes/{uuid}", uuid1)
                .with(jwt())
                .content(objectMapper.writeValueAsString(n3dto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenNoAppUser_whenUpdateNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/notes/{uuid}", uuid1)
                .with(jwt())
                .content(objectMapper.writeValueAsString(n5dto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenNote_whenDeleteNote_thenNoContent() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.deleteNote(uuid1, au1)).willReturn(true);
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/notes/{uuid}", uuid1).with(jwt()))
        .andExpect(status().isNoContent());
  }

  @Test
  void givenNoNotes_whenDeleteNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.deleteNote(uuid1, au1)).willReturn(false);
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/notes/{uuid}", uuid1).with(jwt()))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenNoAppUser_whenDeleteNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.empty());
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/notes/{uuid}", uuid1).with(jwt()))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenAnotherAppUserNote_whenReadNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au2));
    given(noteService.readNote(uuid1, au2)).willReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/notes/{uuid}", uuid1)
                .with(jwt())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenTagAndNote_whenAddTagToNote_thenNoContent() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.addTagToNote(uuid1, uuid2, au1)).willReturn(true);
    mockMvc
        .perform(MockMvcRequestBuilders.put("/notes/{uuid}/tags/{uuid}", uuid1, uuid2).with(jwt()))
        .andExpect(status().isNoContent());
  }

  @Test
  void givenNoTagsAndNoNotes_whenAddTagToNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.addTagToNote(uuid1, uuid2, au1)).willReturn(false);
    mockMvc
        .perform(MockMvcRequestBuilders.put("/notes/{uuid}/tags/{uuid}", uuid1, uuid2).with(jwt()))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenNoAppUser_whenAddTagToNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.empty());
    mockMvc
        .perform(MockMvcRequestBuilders.put("/notes/{uuid}/tags/{uuid}", uuid1, uuid2).with(jwt()))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenTagAndNote_whenRemoveTagFromNote_thenNoContent() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.removeTagFromNote(uuid1, uuid2, au1)).willReturn(true);
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/notes/{uuid}/tags/{uuid}", uuid1, uuid2).with(jwt()))
        .andExpect(status().isNoContent());
  }

  @Test
  void givenNoTagsAndNoNotes_whenRemoveTagFromNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.removeTagFromNote(uuid1, uuid2, au1)).willReturn(false);
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/notes/{uuid}/tags/{uuid}", uuid1, uuid2).with(jwt()))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenNoAppUser_whenRemoveTagFromNote_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/notes/{uuid}/tags/{uuid}", uuid1, uuid2).with(jwt()))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenTwoTagsAndNoteUuid_whenReadNoteTags_thenOk() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.readNoteTags(uuid1, au1)).willReturn(Optional.of(Set.of(t1, t2)));
    given(tagDtoMapper.convertToDto(t1)).willReturn(t1dto);
    given(tagDtoMapper.convertToDto(t2)).willReturn(t2dto);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/notes/{uuid}/tags", uuid1).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[*].name", containsInAnyOrder(t1dto.getName(), t2dto.getName())))
        .andExpect(
            jsonPath(
                "$.[*].colorHex", containsInAnyOrder(t1dto.getColorHex(), t2dto.getColorHex())));
  }

  @Test
  void givenNoTagsAndNoNotes_whenReadNoteTags_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.readNoteTags(uuid1, au1)).willReturn(Optional.empty());
    mockMvc
        .perform(MockMvcRequestBuilders.get("/notes/{uuid}/tags", uuid1).with(jwt()))
        .andExpect(status().isNotFound());
  }

  @Test
  void givenNoTagsAndNoteUuid_whenReadNoteTags_thenOk() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
    given(noteService.readNoteTags(uuid1, au1)).willReturn(Optional.of(Collections.emptySet()));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/notes/{uuid}/tags", uuid1).with(jwt()))
        .andExpect(status().isOk())
        .andExpect(content().string("[]"));
  }

  @Test
  void givenNoAppUser_whenReadNoteTags_thenNotFound() throws Exception {
    given(appUserService.getAppUserFromToken()).willReturn(Optional.empty());
    mockMvc
        .perform(MockMvcRequestBuilders.get("/notes/{uuid}/tags", uuid1).with(jwt()))
        .andExpect(status().isNotFound());
  }
}
