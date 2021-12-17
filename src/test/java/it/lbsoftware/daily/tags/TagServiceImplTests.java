package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.notes.Note;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTests {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag t1;
    private Tag t2;
    private AppUser au1;
    private UUID uuid1;
    private Note n1;

    @BeforeEach
    void setUp() {
        t1 = Tag.builder().name("Tag1").colorHex("#112233").noteSet(new HashSet<>()).build();
        t2 = Tag.builder().name("Tag2").colorHex("#223344").noteSet(new HashSet<>()).build();
        au1 = AppUser.builder().uid("123").email("au1@daily.it").build();
        uuid1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        n1 = Note.builder().text("Note1").tagSet(new HashSet<>()).build();
        n1.getTagSet().add(t1);
        t1.getNoteSet().add(n1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void givenNoTagAndAppUser_whenCreateTag_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag(null, au1));
    }

    @Test
    void givenTagAndNoAppUser_whenCreateTag_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag(t1, null));
    }

    @Test
    void givenTagAndAppUser_whenCreateTag_thenReturnTag() {
        given(tagRepository.save(t1)).willReturn(t1);
        Tag res = tagService.createTag(t1, au1);
        verify(tagRepository, times(1)).save(t1);
        assertEquals(t1, res);
    }

    @Test
    void givenNoUuidAndAppUser_whenReadTag_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.readTag(null, au1));
    }

    @Test
    void givenUuidAndNoAppUser_whenReadTag_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.readTag(uuid1, null));
    }

    @Test
    void givenUuidAndAppUser_whenReadTag_thenReturnEmpty() {
        given(tagRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.empty());
        Optional<Tag> res = tagService.readTag(uuid1, au1);
        verify(tagRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertEquals(Optional.empty(), res);
    }

    @Test
    void givenUuidAndAppUser_whenReadTag_thenReturnTag() {
        given(tagRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(t1));
        Optional<Tag> res = tagService.readTag(uuid1, au1);
        verify(tagRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertEquals(Optional.of(t1), res);
    }

    @Test
    void givenNoAppUser_whenReadTags_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.readTags(null));
    }

    @Test
    void givenAppUser_whenReadTags_thenReturnEmpty() {
        given(tagRepository.findByAppUser(au1)).willReturn(Collections.emptyList());
        List<Tag> res = tagService.readTags(au1);
        verify(tagRepository, times(1)).findByAppUser(au1);
        assertEquals(Collections.emptyList(), res);
    }

    @Test
    void givenAppUser_whenReadTags_thenReturnTags() {
        given(tagRepository.findByAppUser(au1)).willReturn(List.of(t1, t2));
        List<Tag> res = tagService.readTags(au1);
        verify(tagRepository, times(1)).findByAppUser(au1);
        assertEquals(List.of(t1, t2), res);
    }

    @Test
    void givenNoUuidAndTagAndAppUser_whenUpdateTag_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.updateTag(null, t1, au1));
    }

    @Test
    void givenUuidAndNoTagAndAppUser_whenUpdateTag_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.updateTag(uuid1, null, au1));
    }

    @Test
    void givenUuidAndTagAndNoAppUser_whenUpdateTag_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.updateTag(uuid1, t1, null));
    }

    @Test
    void givenUuidAndTagAndAppUser_whenUpdateTag_thenReturnEmpty() {
        given(tagRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.empty());
        Optional<Tag> res = tagService.updateTag(uuid1, t1, au1);
        verify(tagRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertEquals(Optional.empty(), res);
    }

    @Test
    void givenUuidAndTagAndAppUser_whenUpdateTag_thenReturnTag() {
        given(tagRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(t1));
        given(tagRepository.save(t1)).willReturn(t1);
        Optional<Tag> res = tagService.updateTag(uuid1, t2, au1);
        verify(tagRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(tagRepository, times(1)).save(t1);
        assertEquals(Optional.of(t1), res);
        assertEquals(t2.getName(), res.get().getName());
        assertEquals(t2.getColorHex(), res.get().getColorHex());
    }

    @Test
    void givenNoUuidAndAppUser_whenDeleteTag_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.deleteTag(null, au1));
    }

    @Test
    void givenUuidAndNoAppUser_whenDeleteTag_thenIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> tagService.deleteTag(uuid1, null));
    }

    @Test
    void givenUuidAndAppUser_whenDeleteTag_thenReturnFalse() {
        given(tagRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.empty());
        Boolean res = tagService.deleteTag(uuid1, au1);
        verify(tagRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        assertFalse(res);
    }

    @Test
    void givenUuidAndAppUser_whenDeleteTag_thenReturnTrue() {
        given(tagRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(t1));
        Boolean res = tagService.deleteTag(uuid1, au1);
        verify(tagRepository, times(1)).findByUuidAndAppUser(uuid1, au1);
        verify(tagRepository, times(1)).delete(t1);
        assertTrue(res);
    }

    @Test
    void givenUuidAndAppUser_whenDeleteTag_thenDeleteNoteTagAssociations() {
        given(tagRepository.findByUuidAndAppUser(uuid1, au1)).willReturn(Optional.of(t1));
        tagService.deleteTag(uuid1, au1);
        assertEquals(0, n1.getTagSet().size());
    }

}