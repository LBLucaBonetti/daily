package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.tags.Tag;
import it.lbsoftware.daily.tags.TagService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTests {

    @Mock
    private NoteRepository noteRepository;
    @Mock
    private TagService tagService;

    @InjectMocks
    private NoteServiceImpl noteService;

    private Note n1;
    private Note n2;
    private AppUser au1;
    private UUID uuid1;
    private UUID uuid2;
    private Tag t1;

    @BeforeEach
    void setUp() {
        n1 = Note.builder().text("Note1").build();
        n2 = Note.builder().text("Note2").build();
        au1 = AppUser.builder().uid("123").email("au1@daily.it").build();
        uuid1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        uuid2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        t1 = Tag.builder().name("Tag1").colorHex("#112233").build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void givenNoNoteAndAppUser_whenCreateNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.createNote(null, au1));
    }

    @Test
    void givenNoteAndNoAppUser_whenCreateNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.createNote(n1, null));
    }

    @Test
    void givenNoteAndAppUser_whenCreateNote_thenReturnNote() {
        given(noteRepository.save(n1)).willReturn(n1);
        Note res = noteService.createNote(n1, au1);
        verify(noteRepository, times(1)).save(n1);
        assertEquals(n1, res);
    }

    @Test
    void givenNoUuidAndAppUser_whenReadNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.readNote(null, au1));
    }

    @Test
    void givenUuidAndNoAppUser_whenReadNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.readNote(uuid1, null));
    }

    @Test
    void givenUuidAndAppUser_whenReadNote_thenReturnEmpty() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.empty());
        Optional<Note> res = noteService.readNote(uuid1, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertEquals(Optional.empty(), res);
    }

    @Test
    void givenUuidAndAppUser_whenReadNote_thenReturnNote() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        Optional<Note> res = noteService.readNote(uuid1, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertEquals(Optional.of(n1), res);
    }

    @Test
    void givenNoAppUser_whenReadNotes_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.readNotes(null));
    }

    @Test
    void givenAppUser_whenReadNotes_thenReturnEmpty() {
        given(noteRepository.findByAppUser(au1)).willReturn(Collections.emptyList());
        List<Note> res = noteService.readNotes(au1);
        verify(noteRepository, times(1)).findByAppUser(au1);
        assertEquals(Collections.emptyList(), res);
    }

    @Test
    void givenAppUser_whenReadNotes_thenReturnNotes() {
        given(noteRepository.findByAppUser(au1)).willReturn(List.of(n1, n2));
        List<Note> res = noteService.readNotes(au1);
        verify(noteRepository, times(1)).findByAppUser(au1);
        assertEquals(List.of(n1, n2), res);
    }

    @Test
    void givenNoUuidAndNoteAndAppUser_whenUpdateNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.updateNote(null, n1, au1));
    }

    @Test
    void givenUuidAndNoNoteAndAppUser_whenUpdateNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.updateNote(uuid1, null, au1));
    }

    @Test
    void givenUuidAndNoteAndNoAppUser_whenUpdateNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.updateNote(uuid1, n1, null));
    }

    @Test
    void givenUuidAndNoteAndAppUser_whenUpdateNote_thenReturnEmpty() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.empty());
        Optional<Note> res = noteService.updateNote(uuid1, n1, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertEquals(Optional.empty(), res);
    }

    @Test
    void givenUuidAndNoteAndAppUser_whenUpdateNote_thenReturnNote() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        given(noteRepository.save(n1)).willReturn(n1);
        Optional<Note> res = noteService.updateNote(uuid1, n2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(noteRepository, times(1)).save(n1);
        assertEquals(Optional.of(n1), res);
        assertEquals(n2.getText(), res.get().getText());
    }

    @Test
    void givenNoUuidAndAppUser_whenDeleteNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.deleteNote(null, au1));
    }

    @Test
    void givenUuidAndNoAppUser_whenDeleteNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.deleteNote(uuid1, null));
    }

    @Test
    void givenUuidAndAppUser_whenDeleteNote_thenReturnFalse() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.empty());
        Boolean res = noteService.deleteNote(uuid1, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertFalse(res);
    }

    @Test
    void givenUuidAndAppUser_whenDeleteNote_thenReturnTrue() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        Boolean res = noteService.deleteNote(uuid1, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(noteRepository, times(1)).delete(n1);
        assertTrue(res);
    }

    @Test
    void givenNoUuidAndTagUuidAndAppUser_whenAddTagToNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.addTagToNote(null, uuid1, au1));
    }

    @Test
    void givenUuidAndNoTagUuidAndAppUser_whenAddTagToNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.addTagToNote(uuid1, null, au1));
    }

    @Test
    void givenUuidAndTagUuidAndNoAppUser_whenAddTagToNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.addTagToNote(uuid1, uuid2, null));
    }

    @Test
    void givenUuidAndTagUuidAndAppUser_whenAddTagToNote_thenReturnFalseBecauseOfNoNote() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.empty());
        Boolean res = noteService.addTagToNote(uuid1, uuid2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertFalse(res);
    }

    @Test
    void givenUuidAndTagUuidAndAppUser_whenAddTagToNote_thenReturnFalseBecauseOfNoTag() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        given(tagService.readTag(uuid2, au1)).willReturn(Optional.empty());
        Boolean res = noteService.addTagToNote(uuid1, uuid2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(tagService, times(1)).readTag(uuid2, au1);
        assertFalse(res);
    }

    @Test
    void givenUuidAndTagUuidAndAppUser_whenAddTagToNote_thenReturnTrue() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        given(tagService.readTag(uuid2, au1)).willReturn(Optional.of(t1));
        Boolean res = noteService.addTagToNote(uuid1, uuid2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(tagService, times(1)).readTag(uuid2, au1);
        verify(noteRepository, times(1)).save(n1);
        assertTrue(res);
    }

    @Test
    void givenUuidAndTagUuidAndAppUser_whenAddTagToNote_thenTagSetHasNote() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        given(tagService.readTag(uuid2, au1)).willReturn(Optional.of(t1));
        noteService.addTagToNote(uuid1, uuid2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(tagService, times(1)).readTag(uuid2, au1);
        verify(noteRepository, times(1)).save(n1);
        assertTrue(n1.getTagSet().contains(t1) && n1.getTagSet().size() == 1);
    }

    @Test
    void givenUuidAndTagUuidAndAppUser_whenAddTagToNote_thenNoteSetHasTag() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        given(tagService.readTag(uuid2, au1)).willReturn(Optional.of(t1));
        noteService.addTagToNote(uuid1, uuid2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(tagService, times(1)).readTag(uuid2, au1);
        verify(noteRepository, times(1)).save(n1);
        assertTrue(t1.getNoteSet().contains(n1) && t1.getNoteSet().size() == 1);
    }

    @Test
    void givenNoUuidAndTagUuidAndAppUser_whenRemoveTagFromNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.removeTagFromNote(null, uuid1, au1));
    }

    @Test
    void givenUuidAndNoTagUuidAndAppUser_whenRemoveTagFromNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.removeTagFromNote(uuid1, null, au1));
    }

    @Test
    void givenUuidAndTagUuidAndNoAppUser_whenRemoveTagFromNote_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.removeTagFromNote(uuid1, uuid2, null));
    }

    @Test
    void givenUuidAndTagUuidAndAppUser_whenRemoveTagFromNote_thenReturnFalseBecauseOfNoNote() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.empty());
        Boolean res = noteService.removeTagFromNote(uuid1, uuid2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertFalse(res);
    }

    @Test
    void givenUuidAndTagUuidAndAppUser_whenRemoveTagFromNote_thenReturnFalseBecauseOfNoTag() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        given(tagService.readTag(uuid2, au1)).willReturn(Optional.empty());
        Boolean res = noteService.removeTagFromNote(uuid1, uuid2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(tagService, times(1)).readTag(uuid2, au1);
        assertFalse(res);
    }

    @Test
    void givenUuidAndTagUuidAndAppUser_whenRemoveTagFromNote_thenReturnTrue() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        given(tagService.readTag(uuid2, au1)).willReturn(Optional.of(t1));
        Boolean res = noteService.removeTagFromNote(uuid1, uuid2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(tagService, times(1)).readTag(uuid2, au1);
        verify(noteRepository, times(1)).save(n1);
        assertTrue(res);
    }

    @Test
    void givenUuidAndTagUuidAndAppUser_whenRemoveTagFromNote_thenTagSetHasNotTag() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        given(tagService.readTag(uuid2, au1)).willReturn(Optional.of(t1));
        noteService.removeTagFromNote(uuid1, uuid2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(tagService, times(1)).readTag(uuid2, au1);
        verify(noteRepository, times(1)).save(n1);
        assertTrue(!n1.getTagSet().contains(t1) && n1.getTagSet().size() == 0);
    }

    @Test
    void givenUuidAndTagUuidAndAppUser_whenRemoveTagFromNote_thenNoteSetHasNotNote() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        given(tagService.readTag(uuid2, au1)).willReturn(Optional.of(t1));
        noteService.removeTagFromNote(uuid1, uuid2, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(tagService, times(1)).readTag(uuid2, au1);
        verify(noteRepository, times(1)).save(n1);
        assertTrue(!t1.getNoteSet().contains(n1) && t1.getNoteSet().size() == 0);
    }

    @Test
    void givenNoUuidAndAppUser_whenReadNoteTags_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.readNoteTags(null, au1));
    }

    @Test
    void givenUuidAndNoAppUser_whenReadNoteTags_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> noteService.readNoteTags(uuid1, null));
    }

    @Test
    void givenUuidAndAppUser_whenReadNoteTags_thenReturnEmpty() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.empty());
        Optional<Set<Tag>> res = noteService.readNoteTags(uuid1, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertEquals(Optional.empty(), res);
    }

    @Test
    void givenUuidAndAppUser_whenReadNoteTags_thenReturnTagSet() {
        given(noteRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(n1));
        t1.addToNote(n1);
        Optional<Set<Tag>> res = noteService.readNoteTags(uuid1, au1);
        verify(noteRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertEquals(Optional.of(Set.of(t1)), res);
    }

}