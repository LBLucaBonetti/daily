package it.lbsoftware.daily.tags;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_FULLNAME;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserTestUtils;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.notes.NoteRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("Tag search integration tests")
class TagSearchIntegrationTests extends DailyAbstractIntegrationTests {

  private static final String BASE_URL = "/api/tags";
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TagRepository tagRepository;
  @Autowired private TagDtoMapper tagDtoMapper;
  @Autowired private NoteRepository noteRepository;
  @Autowired private AppUserRepository appUserRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should search tags by name, case insensitive, containing")
  void test1() throws Exception {
    // Given
    var name = "name";
    var otherName = "otherName";
    var wrongName = "wrong";
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    final var appUser2 =
        AppUserTestUtils.saveOauth2OtherAppUser(appUserRepository, passwordEncoder);
    TagDto tagDto1 =
        tagDtoMapper.convertToDto(
            tagRepository.save(createTag(name, "#123456", Collections.emptySet(), appUser)));
    TagDto tagDto2 =
        tagDtoMapper.convertToDto(
            tagRepository.save(createTag(otherName, "#123456", Collections.emptySet(), appUser)));
    TagDto tagDto3 =
        tagDtoMapper.convertToDto(
            tagRepository.save(createTag(wrongName, "#123456", Collections.emptySet(), appUser)));
    TagDto tagDto4 =
        tagDtoMapper.convertToDto(
            tagRepository.save(createTag(name, "#123456", Collections.emptySet(), appUser2)));

    // When
    PageDto<TagDto> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", name)
                        .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    List<TagDto> tagDtos = res.getContent();
    assertFalse(tagDtos.isEmpty());
    assertEquals(2, tagDtos.size());
    assertThat(tagDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(tagDto1);
    assertThat(tagDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(tagDto2);
    assertThat(tagDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .doesNotContain(tagDto3);
    assertThat(tagDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .doesNotContain(tagDto4);
  }

  @Test
  @DisplayName("Should search tags by color hex, case insensitive, matching exact")
  void test2() throws Exception {
    // Given
    var colorHex = "#abcdef";
    var otherColorHex = "#ABCDEF";
    var wrongColorHex = "#123456";
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    final var appUser2 =
        AppUserTestUtils.saveOauth2OtherAppUser(appUserRepository, passwordEncoder);
    TagDto tagDto1 =
        tagDtoMapper.convertToDto(
            tagRepository.save(createTag("name", colorHex, Collections.emptySet(), appUser)));
    TagDto tagDto2 =
        tagDtoMapper.convertToDto(
            tagRepository.save(createTag("name", otherColorHex, Collections.emptySet(), appUser)));
    TagDto tagDto3 =
        tagDtoMapper.convertToDto(
            tagRepository.save(createTag("name", wrongColorHex, Collections.emptySet(), appUser)));
    TagDto tagDto4 =
        tagDtoMapper.convertToDto(
            tagRepository.save(createTag("name", colorHex, Collections.emptySet(), appUser2)));

    // When
    PageDto<TagDto> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("color-hex", colorHex)
                        .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    List<TagDto> tagDtos = res.getContent();
    assertFalse(tagDtos.isEmpty());
    assertEquals(2, tagDtos.size());
    assertThat(tagDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(tagDto1);
    assertThat(tagDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(tagDto2);
    assertThat(tagDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .doesNotContain(tagDto3);
    assertThat(tagDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .doesNotContain(tagDto4);
  }
}
