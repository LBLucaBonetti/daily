package it.lbsoftware.daily.notes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.tags.Tag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {NoteServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class NoteServiceImplTest {
    @MockBean
    private NoteRepository noteRepository;

    @Autowired
    private NoteServiceImpl noteServiceImpl;

    @Test
    public void testCreateNote() {
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
        when(this.noteRepository.save((Note) any())).thenReturn(note);

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
        Note actualCreateNoteResult = this.noteServiceImpl.createNote(note1, appUser2);
        assertSame(note, actualCreateNoteResult);
        verify(this.noteRepository).save((Note) any());
        AppUser expectedAppUser = actualCreateNoteResult.getAppUser();
        assertEquals(expectedAppUser, note1.getAppUser());
    }

    @Test
    public void testReadNote() {
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
        when(this.noteRepository.findByUuidAndAppUser((UUID) any(), (AppUser) any())).thenReturn(ofResult);
        UUID uuid = UUID.randomUUID();

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
        Optional<Note> actualReadNoteResult = this.noteServiceImpl.readNote(uuid, appUser1);
        assertSame(ofResult, actualReadNoteResult);
        assertTrue(actualReadNoteResult.isPresent());
        verify(this.noteRepository).findByUuidAndAppUser((UUID) any(), (AppUser) any());
    }

    @Test
    public void testReadNotes() {
        ArrayList<Note> noteList = new ArrayList<Note>();
        when(this.noteRepository.findByAppUser((AppUser) any())).thenReturn(noteList);

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
        List<Note> actualReadNotesResult = this.noteServiceImpl.readNotes(appUser);
        assertSame(noteList, actualReadNotesResult);
        assertTrue(actualReadNotesResult.isEmpty());
        verify(this.noteRepository).findByAppUser((AppUser) any());
    }

    @Test
    public void testUpdateNote() {
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
        when(this.noteRepository.save((Note) any())).thenReturn(note1);
        when(this.noteRepository.findByUuidAndAppUser((UUID) any(), (AppUser) any())).thenReturn(ofResult);
        UUID uuid = UUID.randomUUID();

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

        Note note2 = new Note();
        note2.setText("Text");
        note2.setTagSet(new HashSet<Tag>());
        note2.setAppUser(appUser2);
        note2.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        note2.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        note2.setId(123L);
        note2.setUuid(UUID.randomUUID());
        note2.setVersion(1);

        AppUser appUser3 = new AppUser();
        appUser3.setEmail("jane.doe@example.org");
        appUser3.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser3.setNoteList(new ArrayList<Note>());
        appUser3.setUid("1234");
        appUser3.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser3.setTagList(new ArrayList<Tag>());
        appUser3.setId(123L);
        appUser3.setUuid(UUID.randomUUID());
        appUser3.setVersion(1);
        assertTrue(this.noteServiceImpl.updateNote(uuid, note2, appUser3).isPresent());
        verify(this.noteRepository).findByUuidAndAppUser((UUID) any(), (AppUser) any());
        verify(this.noteRepository).save((Note) any());
    }

    @Test
    public void testDeleteNote() {
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
        doNothing().when(this.noteRepository).delete((Note) any());
        when(this.noteRepository.findByUuidAndAppUser((UUID) any(), (AppUser) any())).thenReturn(ofResult);
        UUID uuid = UUID.randomUUID();

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
        assertTrue(this.noteServiceImpl.deleteNote(uuid, appUser1));
        verify(this.noteRepository).delete((Note) any());
        verify(this.noteRepository).findByUuidAndAppUser((UUID) any(), (AppUser) any());
    }

    @Test
    public void testDeleteNote2() {
        doNothing().when(this.noteRepository).delete((Note) any());
        when(this.noteRepository.findByUuidAndAppUser((UUID) any(), (AppUser) any())).thenReturn(Optional.<Note>empty());
        UUID uuid = UUID.randomUUID();

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
        assertFalse(this.noteServiceImpl.deleteNote(uuid, appUser));
        verify(this.noteRepository).findByUuidAndAppUser((UUID) any(), (AppUser) any());
    }
}

