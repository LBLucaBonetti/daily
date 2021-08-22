package it.lbsoftware.daily.notes;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.entities.DtoEntityMappingConverter;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {NoteController.class})
@ExtendWith(SpringExtension.class)
public class NoteControllerTest {
    @MockBean
    private AppUserService appUserService;

    @MockBean
    private DtoEntityMappingConverter<NoteDtoOut, Note> dtoEntityMappingConverter;

    @MockBean
    private DtoEntityMappingConverter<NoteDtoIn, Note> dtoEntityMappingConverter1;

    @Autowired
    private NoteController noteController;

    @MockBean
    private NoteService noteService;

    @MockBean
    private TagService tagService;

    @Test
    public void testReadNote() throws Exception {
        when(this.noteService.readNote((UUID) any(), (AppUser) any())).thenReturn(Optional.<Note>empty());

        NoteDtoOut noteDtoOut = new NoteDtoOut();
        noteDtoOut.setText("Text");
        noteDtoOut.setTagSet(new HashSet<TagDto>());
        noteDtoOut.setUuid(UUID.randomUUID());
        when(this.dtoEntityMappingConverter.convertToDto((Object) any(), (Class<NoteDtoOut>) any())).thenReturn(noteDtoOut);

        AppUser appUser = new AppUser();
        appUser.setEmail("jane.doe@example.org");
        appUser.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setNoteList(new ArrayList<Note>());
        appUser.setUid("1234");
        appUser.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setTagList(new ArrayList<Tag>());
        appUser.setId(123L);
        appUser.setUuid(UUID.randomUUID());
        appUser.setVersion(1);
        when(this.appUserService.getAppUserFromToken()).thenReturn(appUser);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/notes/{uuid}", UUID.randomUUID());
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.noteController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testReadNotes() throws Exception {
        when(this.noteService.readNotes((AppUser) any())).thenReturn(new ArrayList<Note>());

        AppUser appUser = new AppUser();
        appUser.setEmail("jane.doe@example.org");
        appUser.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setNoteList(new ArrayList<Note>());
        appUser.setUid("1234");
        appUser.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setTagList(new ArrayList<Tag>());
        appUser.setId(123L);
        appUser.setUuid(UUID.randomUUID());
        appUser.setVersion(1);
        when(this.appUserService.getAppUserFromToken()).thenReturn(appUser);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/notes");
        MockMvcBuilders.standaloneSetup(this.noteController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    public void testUpdateNote() throws Exception {
        AppUser appUser = new AppUser();
        appUser.setEmail("jane.doe@example.org");
        appUser.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setNoteList(new ArrayList<Note>());
        appUser.setUid("1234");
        appUser.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setTagList(new ArrayList<Tag>());
        appUser.setId(123L);
        appUser.setUuid(UUID.randomUUID());
        appUser.setVersion(1);

        Note note = new Note();
        note.setText("Text");
        note.setTagSet(new HashSet<Tag>());
        note.setAppUser(appUser);
        note.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        note.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        note.setId(123L);
        note.setUuid(UUID.randomUUID());
        note.setVersion(1);
        Optional<Note> ofResult = Optional.<Note>of(note);
        when(this.noteService.updateNote((UUID) any(), (Note) any(), (AppUser) any())).thenReturn(ofResult);

        AppUser appUser1 = new AppUser();
        appUser1.setEmail("jane.doe@example.org");
        appUser1.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser1.setNoteList(new ArrayList<Note>());
        appUser1.setUid("1234");
        appUser1.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser1.setTagList(new ArrayList<Tag>());
        appUser1.setId(123L);
        appUser1.setUuid(UUID.randomUUID());
        appUser1.setVersion(1);

        Note note1 = new Note();
        note1.setText("Text");
        note1.setTagSet(new HashSet<Tag>());
        note1.setAppUser(appUser1);
        note1.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        note1.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        note1.setId(123L);
        note1.setUuid(UUID.randomUUID());
        note1.setVersion(1);
        when(this.dtoEntityMappingConverter1.convertToEntity((Object) any(), (Class<Note>) any())).thenReturn(note1);

        AppUser appUser2 = new AppUser();
        appUser2.setEmail("jane.doe@example.org");
        appUser2.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser2.setNoteList(new ArrayList<Note>());
        appUser2.setUid("1234");
        appUser2.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser2.setTagList(new ArrayList<Tag>());
        appUser2.setId(123L);
        appUser2.setUuid(UUID.randomUUID());
        appUser2.setVersion(1);
        when(this.appUserService.getAppUserFromToken()).thenReturn(appUser2);

        NoteDtoIn noteDtoIn = new NoteDtoIn();
        noteDtoIn.setText("Text");
        noteDtoIn.setTagSet(new HashSet<UUID>());
        noteDtoIn.setUuid(UUID.randomUUID());
        String content = (new ObjectMapper()).writeValueAsString(noteDtoIn);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/notes/{uuid}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.noteController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testUpdateNote2() throws Exception {
        when(this.noteService.updateNote((UUID) any(), (Note) any(), (AppUser) any())).thenReturn(Optional.<Note>empty());

        AppUser appUser = new AppUser();
        appUser.setEmail("jane.doe@example.org");
        appUser.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setNoteList(new ArrayList<Note>());
        appUser.setUid("1234");
        appUser.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setTagList(new ArrayList<Tag>());
        appUser.setId(123L);
        appUser.setUuid(UUID.randomUUID());
        appUser.setVersion(1);

        Note note = new Note();
        note.setText("Text");
        note.setTagSet(new HashSet<Tag>());
        note.setAppUser(appUser);
        note.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        note.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        note.setId(123L);
        note.setUuid(UUID.randomUUID());
        note.setVersion(1);
        when(this.dtoEntityMappingConverter1.convertToEntity((Object) any(), (Class<Note>) any())).thenReturn(note);

        AppUser appUser1 = new AppUser();
        appUser1.setEmail("jane.doe@example.org");
        appUser1.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser1.setNoteList(new ArrayList<Note>());
        appUser1.setUid("1234");
        appUser1.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser1.setTagList(new ArrayList<Tag>());
        appUser1.setId(123L);
        appUser1.setUuid(UUID.randomUUID());
        appUser1.setVersion(1);
        when(this.appUserService.getAppUserFromToken()).thenReturn(appUser1);

        NoteDtoIn noteDtoIn = new NoteDtoIn();
        noteDtoIn.setText("Text");
        noteDtoIn.setTagSet(new HashSet<UUID>());
        noteDtoIn.setUuid(UUID.randomUUID());
        String content = (new ObjectMapper()).writeValueAsString(noteDtoIn);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/notes/{uuid}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.noteController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testCreateNote() throws Exception {
        when(this.noteService.readNotes((AppUser) any())).thenReturn(new ArrayList<Note>());

        AppUser appUser = new AppUser();
        appUser.setEmail("jane.doe@example.org");
        appUser.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setNoteList(new ArrayList<Note>());
        appUser.setUid("1234");
        appUser.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setTagList(new ArrayList<Tag>());
        appUser.setId(123L);
        appUser.setUuid(UUID.randomUUID());
        appUser.setVersion(1);
        when(this.appUserService.getAppUserFromToken()).thenReturn(appUser);

        NoteDtoIn noteDtoIn = new NoteDtoIn();
        noteDtoIn.setText("Text");
        noteDtoIn.setTagSet(new HashSet<UUID>());
        noteDtoIn.setUuid(UUID.randomUUID());
        String content = (new ObjectMapper()).writeValueAsString(noteDtoIn);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(this.noteController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    public void testDeleteNote() throws Exception {
        when(this.noteService.deleteNote((UUID) any(), (AppUser) any())).thenReturn(true);

        AppUser appUser = new AppUser();
        appUser.setEmail("jane.doe@example.org");
        appUser.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setNoteList(new ArrayList<Note>());
        appUser.setUid("1234");
        appUser.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setTagList(new ArrayList<Tag>());
        appUser.setId(123L);
        appUser.setUuid(UUID.randomUUID());
        appUser.setVersion(1);
        when(this.appUserService.getAppUserFromToken()).thenReturn(appUser);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/notes/{uuid}", UUID.randomUUID());
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.noteController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDeleteNote2() throws Exception {
        when(this.noteService.deleteNote((UUID) any(), (AppUser) any())).thenReturn(false);

        AppUser appUser = new AppUser();
        appUser.setEmail("jane.doe@example.org");
        appUser.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setNoteList(new ArrayList<Note>());
        appUser.setUid("1234");
        appUser.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser.setTagList(new ArrayList<Tag>());
        appUser.setId(123L);
        appUser.setUuid(UUID.randomUUID());
        appUser.setVersion(1);
        when(this.appUserService.getAppUserFromToken()).thenReturn(appUser);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/notes/{uuid}", UUID.randomUUID());
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.noteController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}

