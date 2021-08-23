package it.lbsoftware.daily.tags;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.entities.DtoEntityMappingConverter;
import it.lbsoftware.daily.notes.Note;

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

@ContextConfiguration(classes = {TagController.class})
@ExtendWith(SpringExtension.class)
public class TagControllerTest {
    @MockBean
    private AppUserService appUserService;

    @MockBean
    private DtoEntityMappingConverter<TagDto, Tag> dtoEntityMappingConverter;

    @Autowired
    private TagController tagController;

    @MockBean
    private TagService tagService;

    @Test
    public void testReadTag() throws Exception {
        when(this.tagService.readTag((UUID) any(), (AppUser) any())).thenReturn(Optional.<Tag>empty());

        TagDto tagDto = new TagDto();
        tagDto.setColorHex("0123456789ABCDEF");
        tagDto.setName("Name");
        tagDto.setUuid(UUID.randomUUID());
        when(this.dtoEntityMappingConverter.convertToDto((Object) any(), (Class<TagDto>) any())).thenReturn(tagDto);

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
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tags/{uuid}", UUID.randomUUID());
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.tagController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testReadTags() throws Exception {
        when(this.tagService.readTags((AppUser) any())).thenReturn(new ArrayList<Tag>());

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
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tags");
        MockMvcBuilders.standaloneSetup(this.tagController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    public void testUpdateTag() throws Exception {
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
        when(this.tagService.updateTag((UUID) any(), (Tag) any(), (AppUser) any())).thenReturn(ofResult);

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
        when(this.dtoEntityMappingConverter.convertToEntity((Object) any(), (Class<Tag>) any())).thenReturn(tag1);

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

        TagDto tagDto = new TagDto();
        tagDto.setColorHex("#012DEF");
        tagDto.setName("Name");
        tagDto.setUuid(UUID.randomUUID());
        String content = (new ObjectMapper()).writeValueAsString(tagDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/tags/{uuid}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.tagController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testUpdateTag2() throws Exception {
        when(this.tagService.updateTag((UUID) any(), (Tag) any(), (AppUser) any())).thenReturn(Optional.<Tag>empty());

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
        when(this.dtoEntityMappingConverter.convertToEntity((Object) any(), (Class<Tag>) any())).thenReturn(tag);

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

        TagDto tagDto = new TagDto();
        tagDto.setColorHex("#FFFFFf");
        tagDto.setName("Name");
        tagDto.setUuid(UUID.randomUUID());
        String content = (new ObjectMapper()).writeValueAsString(tagDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/tags/{uuid}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.tagController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testCreateTag() throws Exception {
        when(this.tagService.readTags((AppUser) any())).thenReturn(new ArrayList<Tag>());

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

        TagDto tagDto = new TagDto();
        tagDto.setColorHex("0123456789ABCDEF");
        tagDto.setName("Name");
        tagDto.setUuid(UUID.randomUUID());
        String content = (new ObjectMapper()).writeValueAsString(tagDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(this.tagController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    public void testDeleteTag() throws Exception {
        when(this.tagService.deleteTag((UUID) any(), (AppUser) any())).thenReturn(true);

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
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/tags/{uuid}", UUID.randomUUID());
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.tagController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testDeleteTag2() throws Exception {
        when(this.tagService.deleteTag((UUID) any(), (AppUser) any())).thenReturn(false);

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
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/tags/{uuid}", UUID.randomUUID());
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(this.tagController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}

