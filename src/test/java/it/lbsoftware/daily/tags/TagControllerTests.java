package it.lbsoftware.daily.tags;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.hamcrest.Matchers.containsInAnyOrder;
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

    private Tag t1;
    private Tag t2;
    private TagDto t1dto;
    private TagDto t2dto;
    private TagDto t3dto;
    private TagDto t4dto;
    private AppUser au1;
    private AppUser au2;
    private UUID uuid1;

    @BeforeEach
    void setUp() {
        t1 = Tag.builder().name("Tag1").colorHex("#112233").noteSet(new HashSet<>()).build();
        t2 = Tag.builder().name("Tag2").colorHex("#223344").noteSet(new HashSet<>()).build();
        uuid1 = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID uuid2 = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        t1dto = new TagDto();
        t1dto.setName(t1.getName());
        t1dto.setColorHex(t1.getColorHex());
        t1dto.setUuid(uuid1);
        t2dto = new TagDto();
        t2dto.setName(t2.getName());
        t2dto.setColorHex(t2.getColorHex());
        t2dto.setUuid(uuid2);
        t3dto = new TagDto();
        t3dto.setName(t1.getName());
        t3dto.setColorHex("112233");
        t3dto.setUuid(uuid1);
        t4dto = new TagDto();
        t4dto.setName("");
        t4dto.setColorHex(t1.getColorHex());
        t4dto.setUuid(uuid1);
        au1 = AppUser.builder().uid("123").email("au1@daily.it").noteList(new ArrayList<>()).tagList(new ArrayList<>()).build();
        au2 = AppUser.builder().uid("234").email("au2@daily.it").noteList(new ArrayList<>()).tagList(new ArrayList<>()).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void givenNoAuth_whenReadTags_thenUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/tags").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenNoTags_whenReadTags_thenOk() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
        given(tagService.readTags(au1)).willReturn(Collections.emptyList());
        mockMvc.perform(MockMvcRequestBuilders.get("/tags").with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void givenTwoTags_whenReadTags_thenOk() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
        given(tagService.readTags(au1)).willReturn(List.of(t1, t2));
        given(tagDtoMapper.convertToDto(t1)).willReturn(t1dto);
        given(tagDtoMapper.convertToDto(t2)).willReturn(t2dto);
        mockMvc.perform(MockMvcRequestBuilders.get("/tags").with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].name", containsInAnyOrder(t1dto.getName(), t2dto.getName())))
                .andExpect(jsonPath("$.[*].colorHex", containsInAnyOrder(t1dto.getColorHex(), t2dto.getColorHex())))
                .andExpect(jsonPath("$.[*].uuid", containsInAnyOrder(t1dto.getUuid().toString(), t2dto.getUuid().toString())));
    }

    @Test
    void givenTag_whenCreateTag_thenCreated() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
        given(tagDtoMapper.convertToEntity(t1dto)).willReturn(t1);
        given(tagService.createTag(t1, au1)).willReturn(t2);
        given(tagDtoMapper.convertToDto(t2)).willReturn(t2dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/tags").with(jwt()).content(objectMapper.writeValueAsString(t1dto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(t2dto.getName()))
                .andExpect(jsonPath("$.colorHex").value(t2dto.getColorHex()))
                .andExpect(jsonPath("$.uuid").value(t2dto.getUuid().toString()));
    }

    @Test
    void givenWrongColorHexTag_whenCreateTag_thenBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tags").with(jwt()).content(objectMapper.writeValueAsString(t3dto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWrongNameTag_whenCreateTag_thenBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tags").with(jwt()).content(objectMapper.writeValueAsString(t4dto)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenTag_whenReadTag_thenOk() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
        given(tagService.readTag(uuid1, au1)).willReturn(Optional.of(t1));
        given(tagDtoMapper.convertToDto(t1)).willReturn(t1dto);
        mockMvc.perform(MockMvcRequestBuilders.get("/tags/{uuid}", uuid1).with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(t1dto.getName()))
                .andExpect(jsonPath("$.colorHex").value(t1dto.getColorHex()))
                .andExpect(jsonPath("$.uuid").value(t1dto.getUuid().toString()));
    }

    @Test
    void givenNoTags_whenReadTag_thenNotFound() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
        given(tagService.readTag(uuid1, au1)).willReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/tags/{uuid}", uuid1).with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenTag_whenUpdateTag_thenNoContent() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
        given(tagDtoMapper.convertToEntity(t1dto)).willReturn(t1);
        given(tagService.updateTag(uuid1, t1, au1)).willReturn(Optional.of(t2));
        given(tagDtoMapper.convertToDto(t2)).willReturn(t2dto);
        mockMvc.perform(MockMvcRequestBuilders.put("/tags/{uuid}", uuid1).with(jwt()).content(objectMapper.writeValueAsString(t1dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenWrongColorHexTag_whenUpdateTag_thenBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/tags/{uuid}", uuid1).with(jwt()).content(objectMapper.writeValueAsString(t3dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenWrongNameTag_whenUpdateTag_thenBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/tags/{uuid}", uuid1).with(jwt()).content(objectMapper.writeValueAsString(t4dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNoTags_whenUpdateTag_thenNotFound() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
        given(tagDtoMapper.convertToEntity(t1dto)).willReturn(t1);
        given(tagService.updateTag(uuid1, t1, au1)).willReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.put("/tags/{uuid}", uuid1).with(jwt()).content(objectMapper.writeValueAsString(t1dto)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenTag_whenDeleteTag_thenNoContent() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
        given(tagService.deleteTag(uuid1, au1)).willReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/tags/{uuid}", uuid1).with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNoTags_whenDeleteTag_thenNotFound() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au1));
        given(tagService.deleteTag(uuid1, au1)).willReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.delete("/tags/{uuid}", uuid1).with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAnotherAppUserTag_whenReadTag_thenNotFound() throws Exception {
        given(appUserService.getAppUserFromToken()).willReturn(Optional.of(au2));
        given(tagService.readTag(uuid1, au2)).willReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/tags/{uuid}", uuid1).with(jwt()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}