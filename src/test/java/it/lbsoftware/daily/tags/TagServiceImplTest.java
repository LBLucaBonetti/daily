package it.lbsoftware.daily.tags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.notes.Note;

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

@ContextConfiguration(classes = {TagServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class TagServiceImplTest {
    @MockBean
    private TagRepository tagRepository;

    @Autowired
    private TagServiceImpl tagServiceImpl;

    @Test
    public void testCreateTag() {
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

        Tag tag = new Tag();
        tag.setAppUser(appUser);
        tag.setNoteSet(new HashSet<Note>());
        tag.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag.setColorHex("0123456789ABCDEF");
        tag.setId(123L);
        tag.setName("Name");
        tag.setUuid(UUID.randomUUID());
        tag.setVersion(1);
        when(this.tagRepository.save((Tag) any())).thenReturn(tag);

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

        Tag tag1 = new Tag();
        tag1.setAppUser(appUser1);
        tag1.setNoteSet(new HashSet<Note>());
        tag1.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag1.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag1.setColorHex("0123456789ABCDEF");
        tag1.setId(123L);
        tag1.setName("Name");
        tag1.setUuid(UUID.randomUUID());
        tag1.setVersion(1);

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
        assertSame(tag, this.tagServiceImpl.createTag(tag1, appUser2));
        verify(this.tagRepository).save((Tag) any());
        assertEquals(appUser1, tag1.getAppUser());
    }

    @Test
    public void testReadTag() {
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

        Tag tag = new Tag();
        tag.setAppUser(appUser);
        tag.setNoteSet(new HashSet<Note>());
        tag.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag.setColorHex("0123456789ABCDEF");
        tag.setId(123L);
        tag.setName("Name");
        tag.setUuid(UUID.randomUUID());
        tag.setVersion(1);
        Optional<Tag> ofResult = Optional.<Tag>of(tag);
        when(this.tagRepository.findByUuidAndAppUser((UUID) any(), (AppUser) any())).thenReturn(ofResult);
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
        Optional<Tag> actualReadTagResult = this.tagServiceImpl.readTag(uuid, appUser1);
        assertSame(ofResult, actualReadTagResult);
        assertTrue(actualReadTagResult.isPresent());
        verify(this.tagRepository).findByUuidAndAppUser((UUID) any(), (AppUser) any());
    }

    @Test
    public void testReadTags() {
        ArrayList<Tag> tagList = new ArrayList<Tag>();
        when(this.tagRepository.findByAppUser((AppUser) any())).thenReturn(tagList);

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
        List<Tag> actualReadTagsResult = this.tagServiceImpl.readTags(appUser);
        assertSame(tagList, actualReadTagsResult);
        assertTrue(actualReadTagsResult.isEmpty());
        verify(this.tagRepository).findByAppUser((AppUser) any());
    }

    @Test
    public void testUpdateTag() {
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

        Tag tag = new Tag();
        tag.setAppUser(appUser);
        tag.setNoteSet(new HashSet<Note>());
        tag.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag.setColorHex("0123456789ABCDEF");
        tag.setId(123L);
        tag.setName("Name");
        tag.setUuid(UUID.randomUUID());
        tag.setVersion(1);
        Optional<Tag> ofResult = Optional.<Tag>of(tag);

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

        Tag tag1 = new Tag();
        tag1.setAppUser(appUser1);
        tag1.setNoteSet(new HashSet<Note>());
        tag1.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag1.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag1.setColorHex("0123456789ABCDEF");
        tag1.setId(123L);
        tag1.setName("Name");
        tag1.setUuid(UUID.randomUUID());
        tag1.setVersion(1);
        when(this.tagRepository.save((Tag) any())).thenReturn(tag1);
        when(this.tagRepository.findByUuidAndAppUser((UUID) any(), (AppUser) any())).thenReturn(ofResult);
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

        Tag tag2 = new Tag();
        tag2.setAppUser(appUser2);
        tag2.setNoteSet(new HashSet<Note>());
        tag2.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag2.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag2.setColorHex("0123456789ABCDEF");
        tag2.setId(123L);
        tag2.setName("Name");
        tag2.setUuid(UUID.randomUUID());
        tag2.setVersion(1);

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
        assertTrue(this.tagServiceImpl.updateTag(uuid, tag2, appUser3).isPresent());
        verify(this.tagRepository).findByUuidAndAppUser((UUID) any(), (AppUser) any());
        verify(this.tagRepository).save((Tag) any());
    }

    @Test
    public void testDeleteTag() {
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

        Tag tag = new Tag();
        tag.setAppUser(appUser);
        tag.setNoteSet(new HashSet<Note>());
        tag.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag.setColorHex("0123456789ABCDEF");
        tag.setId(123L);
        tag.setName("Name");
        tag.setUuid(UUID.randomUUID());
        tag.setVersion(1);
        Optional<Tag> ofResult = Optional.<Tag>of(tag);
        doNothing().when(this.tagRepository).delete((Tag) any());
        when(this.tagRepository.findByUuidAndAppUser((UUID) any(), (AppUser) any())).thenReturn(ofResult);
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
        assertTrue(this.tagServiceImpl.deleteTag(uuid, appUser1));
        verify(this.tagRepository).delete((Tag) any());
        verify(this.tagRepository).findByUuidAndAppUser((UUID) any(), (AppUser) any());
    }

    @Test
    public void testDeleteTag2() {
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

        AppUser appUser1 = new AppUser();
        appUser1.setEmail("jane.doe@example.org");
        appUser1.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser1.setNoteList(new ArrayList<Note>());
        appUser1.setUid("1234");
        appUser1.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        appUser1.setTagList(new ArrayList<Tag>());
        appUser1.setId(123L);
        appUser1.setUuid(UUID.randomUUID());
        appUser1.setVersion(0);

        Note note = new Note();
        note.setText("Text");
        note.setTagSet(new HashSet<Tag>());
        note.setAppUser(appUser1);
        note.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        note.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        note.setId(123L);
        note.setUuid(UUID.randomUUID());
        note.setVersion(0);

        HashSet<Note> noteSet = new HashSet<Note>();
        noteSet.add(note);

        Tag tag = new Tag();
        tag.setAppUser(appUser);
        tag.setNoteSet(noteSet);
        tag.setCreatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag.setUpdatedAt(LocalDateTime.of(1, 1, 1, 1, 1));
        tag.setColorHex("0123456789ABCDEF");
        tag.setId(123L);
        tag.setName("Name");
        tag.setUuid(UUID.randomUUID());
        tag.setVersion(1);
        Optional<Tag> ofResult = Optional.<Tag>of(tag);
        doNothing().when(this.tagRepository).delete((Tag) any());
        when(this.tagRepository.findByUuidAndAppUser((UUID) any(), (AppUser) any())).thenReturn(ofResult);
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
        assertTrue(this.tagServiceImpl.deleteTag(uuid, appUser2));
        verify(this.tagRepository).delete((Tag) any());
        verify(this.tagRepository).findByUuidAndAppUser((UUID) any(), (AppUser) any());
    }

    @Test
    public void testDeleteTag3() {
        doNothing().when(this.tagRepository).delete((Tag) any());
        when(this.tagRepository.findByUuidAndAppUser((UUID) any(), (AppUser) any())).thenReturn(Optional.<Tag>empty());
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
        assertFalse(this.tagServiceImpl.deleteTag(uuid, appUser));
        verify(this.tagRepository).findByUuidAndAppUser((UUID) any(), (AppUser) any());
    }
}

