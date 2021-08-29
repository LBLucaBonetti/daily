package it.lbsoftware.daily.notes;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.tags.TagDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NoteController.class)
public class NoteControllerTest {

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
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes").with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void givenTwoNotes_whenGetNotes_thenOk() throws Exception {
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
        this.mockMvc.perform(MockMvcRequestBuilders.get("/notes/{uuid}", UUID.randomUUID()).with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNote_whenUpdateNote_thenNoContent() throws Exception {
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
        NoteDto n1dto = new NoteDto();
        n1dto.setText("Note1");
        Note n1 = Note.builder().text("").build();
        given(this.noteDtoMapper.convertToEntity(n1dto)).willReturn(n1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/notes/{uuid}", UUID.randomUUID()).with(jwt()).content(objectMapper.writeValueAsString(n1dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNote_whenDeleteNote_thenNoContent() throws Exception {
        given(this.noteService.deleteNote(any(), any())).willReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/notes/{uuid}", UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNoNotes_whenDeleteNote_thenNotFound() throws Exception {
        given(this.noteService.deleteNote(any(), any())).willReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/notes/{uuid}", UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNotFound());
    }

}
