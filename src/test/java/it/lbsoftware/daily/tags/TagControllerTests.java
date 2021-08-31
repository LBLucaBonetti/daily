package it.lbsoftware.daily.tags;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
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

@WebMvcTest(TagController.class)
class TagControllerTests {

    @MockBean
    private AppUserService appUserService;
    @MockBean
    private TagService tagService;
    @MockBean
    private TagDtoMapper tagDtoMapper;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void givenNoAuth_whenGetTags_thenUnauthorized() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/tags").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenNoTags_whenGetTags_thenOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/tags").with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void givenTwoTags_whenGetTags_thenOk() throws Exception {
        Tag t1 = Tag.builder().name("Tag1").colorHex("#112233").build();
        t1.setUuid(UUID.randomUUID());
        Tag t2 = Tag.builder().name("Tag2").colorHex("#223344").build();
        t2.setUuid(UUID.randomUUID());
        given(this.tagService.readTags(any())).willReturn(List.of(t1, t2));
        TagDto t1dto = new TagDto();
        t1dto.setName(t1.getName());
        t1dto.setColorHex(t1.getColorHex());
        t1dto.setUuid(t1.getUuid());
        TagDto t2dto = new TagDto();
        t2dto.setName(t2.getName());
        t2dto.setColorHex(t2.getColorHex());
        t2dto.setUuid(t2.getUuid());
        given(this.tagDtoMapper.convertToDto(t1)).willReturn(t1dto);
        given(this.tagDtoMapper.convertToDto(t2)).willReturn(t2dto);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/tags").with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value(t1dto.getName()))
                .andExpect(jsonPath("$.[0].colorHex").value(t1dto.getColorHex()))
                .andExpect(jsonPath("$.[0].uuid").value(t1dto.getUuid().toString()))
                .andExpect(jsonPath("$.[1].name").value(t2dto.getName()))
                .andExpect(jsonPath("$.[1].colorHex").value(t2dto.getColorHex()))
                .andExpect(jsonPath("$.[1].uuid").value(t2dto.getUuid().toString()));
    }

    @Test
    void givenTag_whenPostTag_thenCreated() throws Exception {
        TagDto t1dto = new TagDto();
        t1dto.setName("Tag1");
        t1dto.setColorHex("#112233");
        Tag t1 = Tag.builder().name("Tag1").colorHex("#112233").build();
        Tag t2 = Tag.builder().name("Tag1").colorHex("#112233").build();
        t2.setUuid(UUID.randomUUID());
        TagDto t2dto = new TagDto();
        t2dto.setName("Tag1");
        t2dto.setColorHex("#112233");
        t2dto.setUuid(t2.getUuid());
        given(this.tagDtoMapper.convertToEntity(t1dto)).willReturn(t1);
        given(this.tagService.createTag(any(), any())).willReturn(t2);
        given(this.tagDtoMapper.convertToDto(t2)).willReturn(t2dto);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/tags").with(jwt()).content(objectMapper.writeValueAsString(t1dto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(t2dto.getName()))
                .andExpect(jsonPath("$.colorHex").value(t2dto.getColorHex()))
                .andExpect(jsonPath("$.uuid").value(t2dto.getUuid().toString()));
    }

    @Test
    void givenWrongColorHexTag_whenPostTag_thenBadRequest() throws Exception {
        TagDto t1dto = new TagDto();
        t1dto.setName("Tag1");
        t1dto.setColorHex("112233");
        Tag t1 = Tag.builder().name("Tag1").colorHex("112233").build();
        given(this.tagDtoMapper.convertToEntity(t1dto)).willReturn(t1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/tags").with(jwt()).content(objectMapper.writeValueAsString(t1dto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWrongNameTag_whenPostTag_thenBadRequest() throws Exception {
        TagDto t1dto = new TagDto();
        t1dto.setName("");
        t1dto.setColorHex("#112233");
        Tag t1 = Tag.builder().name("").colorHex("#112233").build();
        given(this.tagDtoMapper.convertToEntity(t1dto)).willReturn(t1);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/tags").with(jwt()).content(objectMapper.writeValueAsString(t1dto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenTag_whenGetTag_thenOk() throws Exception {
        Tag t1 = Tag.builder().name("Tag1").colorHex("#112233").build();
        t1.setUuid(UUID.randomUUID());
        given(this.tagService.readTag(any(), any())).willReturn(Optional.of(t1));
        TagDto t1dto = new TagDto();
        t1dto.setName(t1.getName());
        t1dto.setColorHex(t1.getColorHex());
        t1dto.setUuid(t1.getUuid());
        given(this.tagDtoMapper.convertToDto(t1)).willReturn(t1dto);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/tags/{uuid}", t1.getUuid()).with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(t1dto.getName()))
                .andExpect(jsonPath("$.colorHex").value(t1dto.getColorHex()))
                .andExpect(jsonPath("$.uuid").value(t1dto.getUuid().toString()));
    }

    @Test
    void givenNoTags_whenGetTag_thenNotFound() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/tags/{uuid}", UUID.randomUUID()).with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenTag_whenUpdateTag_thenNoContent() throws Exception {
        TagDto t1dto = new TagDto();
        t1dto.setName("Tag1");
        t1dto.setColorHex("#112233");
        Tag t1 = Tag.builder().name("Tag1").colorHex("#112233").build();
        given(this.tagDtoMapper.convertToEntity(t1dto)).willReturn(t1);
        Tag t2 = Tag.builder().name("Tag1Updated").colorHex("#223344").build();
        t2.setUuid(UUID.randomUUID());
        TagDto t2dto = new TagDto();
        t2dto.setName("Tag1Updated");
        t2dto.setColorHex("#223344");
        t2dto.setUuid(t2.getUuid());
        given(this.tagService.updateTag(any(), any(), any())).willReturn(Optional.of(t2));
        given(this.tagDtoMapper.convertToDto(t2)).willReturn(t2dto);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/tags/{uuid}", t2.getUuid()).with(jwt()).content(objectMapper.writeValueAsString(t1dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenWrongColorHexTag_whenUpdateTag_thenBadRequest() throws Exception {
        TagDto t1dto = new TagDto();
        t1dto.setName("Tag1");
        t1dto.setColorHex("112233");
        Tag t1 = Tag.builder().name("Tag1").colorHex("112233").build();
        given(this.tagDtoMapper.convertToEntity(t1dto)).willReturn(t1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/tags/{uuid}", UUID.randomUUID()).with(jwt()).content(objectMapper.writeValueAsString(t1dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWrongNameTag_whenUpdateTag_thenBadRequest() throws Exception {
        TagDto t1dto = new TagDto();
        t1dto.setName("");
        t1dto.setColorHex("#112233");
        Tag t1 = Tag.builder().name("").colorHex("112233").build();
        given(this.tagDtoMapper.convertToEntity(t1dto)).willReturn(t1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/tags/{uuid}", UUID.randomUUID()).with(jwt()).content(objectMapper.writeValueAsString(t1dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNoTags_whenUpdateTag_thenNotFound() throws Exception {
        TagDto t1dto = new TagDto();
        t1dto.setName("Tag1");
        t1dto.setColorHex("#112233");
        Tag t1 = Tag.builder().name("").colorHex("112233").build();
        given(this.tagDtoMapper.convertToEntity(t1dto)).willReturn(t1);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/tags/{uuid}", UUID.randomUUID()).with(jwt()).content(objectMapper.writeValueAsString(t1dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenTag_whenDeleteTag_thenNoContent() throws Exception {
        given(this.tagService.deleteTag(any(), any())).willReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/tags/{uuid}", UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNoTags_whenDeleteTag_thenNotFound() throws Exception {
        given(this.tagService.deleteTag(any(), any())).willReturn(false);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/tags/{uuid}", UUID.randomUUID()).with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAnotherAppUserTag_whenGetTag_thenNotFound() throws Exception {
        AppUser au1 = AppUser.builder().uid("123").email("au1@daily.it").build();
        AppUser au2 = AppUser.builder().uid("234").email("au2@daily.it").build();
        TagDto t1dto = new TagDto();
        t1dto.setName("Tag1");
        t1dto.setColorHex("#112233");
        Tag t1 = Tag.builder().name("Tag1").colorHex("112233").appUser(au1).build();
        t1.setUuid(UUID.randomUUID());
        given(this.appUserService.getAppUserFromToken()).willReturn(au2);
        given(this.tagDtoMapper.convertToEntity(any())).willReturn(t1);
        given(this.tagService.readTag(t1.getUuid(), au2)).willReturn(Optional.empty());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/tags/{uuid}", t1.getUuid()).with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
