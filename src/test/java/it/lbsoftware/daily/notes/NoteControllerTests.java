package it.lbsoftware.daily.notes;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
public class NoteControllerTests {

    @MockBean
    private AppUserService appUserService;
    @MockBean
    private NoteService noteService;
    @MockBean
    private NoteDtoMapper noteDtoMapper;
    @MockBean
    private TagDtoMapper tagDtoMapper;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void givenNoAuth_whenGetNotes_thenUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenNoNotes_whenGetNotes_thenOk() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes").with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void givenTwoNotes_whenGetNotes_thenOk() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        Note n1 = Note.builder().text("Note1").build();
        n1.setUuid(UUID.randomUUID());
        Note n2 = Note.builder().text("Note2").build();
        n2.setUuid(UUID.randomUUID());
        given(this.noteService.readNotes(any())).willReturn(List.of(n1, n2));
        NoteDto n1dto = new NoteDto();
        n1dto.setText(n1.getText());
        n1dto.setUuid(n1.getUuid());
        NoteDto n2dto = new NoteDto();
        n2dto.setText(n2.getText());
        n2dto.setUuid(n2.getUuid());
        given(this.noteDtoMapper.convertToDto(n1)).willReturn(n1dto);
        given(this.noteDtoMapper.convertToDto(n2)).willReturn(n2dto);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes").with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].text").value(n1dto.getText()))
                .andExpect(jsonPath("$.[0].uuid").value(n1dto.getUuid().toString()))
                .andExpect(jsonPath("$.[1].text").value(n2dto.getText()))
                .andExpect(jsonPath("$.[1].uuid").value(n2dto.getUuid().toString()));
    }

    @Test
    void givenNote_whenPostNote_thenCreated() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        NoteDto n1dto = new NoteDto();
        n1dto.setText("Note1");
        Note n1 = Note.builder().text("Note1").build();
        Note n2 = Note.builder().text("Note1").build();
        n2.setUuid(UUID.randomUUID());
        NoteDto n2dto = new NoteDto();
        n2dto.setText("Note1");
        n2dto.setUuid(n2.getUuid());
        given(this.noteDtoMapper.convertToEntity(n1dto)).willReturn(n1);
        given(this.noteService.createNote(any(), any())).willReturn(n2);
        given(this.noteDtoMapper.convertToDto(n2)).willReturn(n2dto);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/notes").with(jwt()).content(objectMapper.writeValueAsString(n1dto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value(n2dto.getText()))
                .andExpect(jsonPath("$.uuid").value(n2dto.getUuid().toString()));
    }

    @Test
    void givenWrongTextNote_whenPostNote_thenBadRequest() throws Exception {
        NoteDto n1dto = new NoteDto();
        n1dto.setText("");
        Note n1 = Note.builder().text("").build();
        given(this.noteDtoMapper.convertToEntity(n1dto)).willReturn(n1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/notes").with(jwt()).content(objectMapper.writeValueAsString(n1dto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNote_whenGetNote_thenOk() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        Note n1 = Note.builder().text("Note1").build();
        n1.setUuid(UUID.randomUUID());
        given(this.noteService.readNote(any(), any())).willReturn(Optional.of(n1));
        NoteDto n1dto = new NoteDto();
        n1dto.setText(n1.getText());
        n1dto.setUuid(n1.getUuid());
        given(this.noteDtoMapper.convertToDto(n1)).willReturn(n1dto);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes/{uuid}", n1.getUuid()).with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(n1dto.getText()))
                .andExpect(jsonPath("$.uuid").value(n1dto.getUuid().toString()));
    }

    @Test
    void givenNoNotes_whenGetNote_thenNotFound() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes/{uuid}", UUID.randomUUID()).with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNote_whenUpdateNote_thenNoContent() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        NoteDto n1dto = new NoteDto();
        n1dto.setText("Note1");
        Note n1 = Note.builder().text("Note1").build();
        given(this.noteDtoMapper.convertToEntity(n1dto)).willReturn(n1);
        Note n2 = Note.builder().text("Note1Updated").build();
        n2.setUuid(UUID.randomUUID());
        NoteDto n2dto = new NoteDto();
        n2dto.setText("Note1Updated");
        n2dto.setUuid(n2.getUuid());
        given(this.noteService.updateNote(any(), any(), any())).willReturn(Optional.of(n2));
        given(this.noteDtoMapper.convertToDto(n2)).willReturn(n2dto);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/notes/{uuid}", n2.getUuid()).with(jwt()).content(objectMapper.writeValueAsString(n1dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenWrongTextNote_whenUpdateNote_thenBadRequest() throws Exception {
        NoteDto n1dto = new NoteDto();
        n1dto.setText("");
        Note n1 = Note.builder().text("").build();
        given(this.noteDtoMapper.convertToEntity(n1dto)).willReturn(n1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/notes/{uuid}", UUID.randomUUID()).with(jwt()).content(objectMapper.writeValueAsString(n1dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNoNotes_whenUpdateNote_thenNotFound() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        NoteDto n1dto = new NoteDto();
        n1dto.setText("Note1");
        Note n1 = Note.builder().text("").build();
        given(this.noteDtoMapper.convertToEntity(n1dto)).willReturn(n1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/notes/{uuid}", UUID.randomUUID()).with(jwt()).content(objectMapper.writeValueAsString(n1dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNote_whenDeleteNote_thenNoContent() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        given(this.noteService.deleteNote(any(), any())).willReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/notes/{uuid}", UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNoNotes_whenDeleteNote_thenNotFound() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        given(this.noteService.deleteNote(any(), any())).willReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/notes/{uuid}", UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAnotherAppUserNote_whenGetNote_thenNotFound() throws Exception {
        AppUser au1 = AppUser.builder().uid("123").email("au1@daily.it").build();
        AppUser au2 = AppUser.builder().uid("234").email("au2@daily.it").build();
        NoteDto n1dto = new NoteDto();
        n1dto.setText("Note1");
        Note n1 = Note.builder().text("Note1").appUser(au1).build();
        n1.setUuid(UUID.randomUUID());
        given(this.appUserService.getAppUserFromToken()).willReturn(Optional.of(au2));
        given(this.noteDtoMapper.convertToEntity(any())).willReturn(n1);
        given(this.noteService.readNote(n1.getUuid(), au2)).willReturn(Optional.empty());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes/{uuid}", n1.getUuid()).with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenTagAndNote_whenAddTagToNote_thenNoContent() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        given(this.noteService.addTagToNote(any(), any(), any())).willReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/notes/{uuid}/tags/{uuid}", UUID.randomUUID(), UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNoTagsAndNotes_whenAddTagToNote_thenNotFound() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        given(this.noteService.addTagToNote(any(), any(), any())).willReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/notes/{uuid}/tags/{uuid}", UUID.randomUUID(), UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenTagAndNote_whenRemoveTagFromNote_thenNoContent() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        given(this.noteService.removeTagFromNote(any(), any(), any())).willReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/notes/{uuid}/tags/{uuid}", UUID.randomUUID(), UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNoTagsAndNotes_whenRemoveTagFromNote_thenNotFound() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        given(this.noteService.removeTagFromNote(any(), any(), any())).willReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/notes/{uuid}/tags/{uuid}", UUID.randomUUID(), UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenTwoTagsAndOneNote_whenGetNoteTags_thenOk() throws Exception {
        Tag t1 = Tag.builder().name("Tag1").colorHex("112233").build();
        TagDto t1dto = new TagDto();
        t1dto.setName("Tag1");
        t1dto.setColorHex("#112233");
        Tag t2 = Tag.builder().name("Tag2").colorHex("223344").build();
        TagDto t2dto = new TagDto();
        t2dto.setName("Tag2");
        t2dto.setColorHex("#223344");
        AppUser au1 = AppUser.builder().uid("123").email("au1@daily.it").build();
        Note n1 = Note.builder().text("Note1").appUser(au1).build();
        n1.setUuid(UUID.randomUUID());
        given(this.appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
        given(this.noteService.readNoteTags(n1.getUuid(), au1)).willReturn(Optional.of(Set.of(t1, t2)));
        given(this.tagDtoMapper.convertToDto(t1)).willReturn(t1dto);
        given(this.tagDtoMapper.convertToDto(t2)).willReturn(t2dto);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes/{uuid}/tags", n1.getUuid()).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].name", containsInAnyOrder(t1dto.getName(), t2dto.getName())))
                .andExpect(jsonPath("$.[*].colorHex", containsInAnyOrder(t1dto.getColorHex(), t2dto.getColorHex())));
    }

    @Test
    void givenNoTagsAndNotes_whenGetNoteTags_thenNotFound() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        given(this.noteService.readNoteTags(any(), any())).willReturn(Optional.empty());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes/{uuid}/tags", UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNoTagsAndOneNote_whenGetNoteTags_thenOk() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(AppUser.builder().build()));
        Note n1 = Note.builder().text("Note1").build();
        n1.setUuid(UUID.randomUUID());
        given(this.noteService.readNoteTags(any(), any())).willReturn(Optional.of(Collections.emptySet()));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes/{uuid}/tags", n1.getUuid()).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

}
