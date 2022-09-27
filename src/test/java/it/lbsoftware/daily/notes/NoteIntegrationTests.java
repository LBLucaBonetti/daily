package it.lbsoftware.daily.notes;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static it.lbsoftware.daily.notes.NoteTestUtils.createNoteDto;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("Note integration tests")
class NoteIntegrationTests extends DailyAbstractIntegrationTests {

  private static final String BASE_URL = "/api/notes";
  private static final String TEXT = "text";
  private static final String APP_USER = "appUser";

  @BeforeEach
  void beforeEach() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();
  }

//  @Test
//  void test() throws Exception {
//    mockMvc.perform(get(BASE_URL).with(loginOf("ciccio")))
//        .andExpect(status().isOk());
//  }

  @Test
  @DisplayName("Should return unauthorized when create note, csrf and no auth")
  void test1() throws Exception {
    mockMvc.perform(post(BASE_URL).with(csrf())).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when read note and no auth")
  void test2() throws Exception {
    mockMvc.perform(get(BASE_URL + "/{uuid}", UUID.randomUUID())).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when read notes and no auth")
  void test3() throws Exception {
    mockMvc.perform(get(BASE_URL)).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when update note, csrf and no auth")
  void test4() throws Exception {
    mockMvc.perform(put(BASE_URL + "/{uuid}", UUID.randomUUID()).with(csrf())).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when delete note, csrf and no auth")
  void test5() throws Exception {
    mockMvc.perform(delete(BASE_URL + "/{uuid}", UUID.randomUUID()).with(csrf())).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when add tag to note, csrf and no auth")
  void test6() throws Exception {
    mockMvc.perform(put(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), UUID.randomUUID()).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when remove tag from note, csrf and no auth")
  void test7() throws Exception {
    mockMvc.perform(delete(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), UUID.randomUUID()).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return unauthorized when read note tags and no auth")
  void test8() throws Exception {
    mockMvc.perform(get(BASE_URL + "/{uuid}/tags", UUID.randomUUID()).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return forbidden when create note, no csrf and no auth")
  void test9() throws Exception {
    mockMvc.perform(post(BASE_URL)).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when update note, no csrf and no auth")
  void test10() throws Exception {
    mockMvc.perform(put(BASE_URL + "/{uuid}", UUID.randomUUID())).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when delete note, no csrf and no auth")
  void test11() throws Exception {
    mockMvc.perform(delete(BASE_URL + "/{uuid}", UUID.randomUUID())).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when add tag to note, no csrf and no auth")
  void test12() throws Exception {
    mockMvc.perform(put(BASE_URL + "/{uuid}/tags/{tagsUuid}", UUID.randomUUID(), UUID.randomUUID())).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return forbidden when remove tag from note, no csrf and no auth")
  void test13() throws Exception {
    mockMvc.perform(delete(BASE_URL + "/{uuid}/tags/{tagsUuid}", UUID.randomUUID(), UUID.randomUUID())).andExpect(status().isForbidden());
  }

}