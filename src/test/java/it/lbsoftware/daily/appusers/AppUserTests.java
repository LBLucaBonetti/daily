package it.lbsoftware.daily.appusers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test", "okta"})
class AppUserTests {

  private AppUser au1;
  private AppUser au2;
  private AppUser au3;

  @BeforeEach
  void setUp() {
    au1 = AppUser.builder().uid("123").email("au1@daily.it").build();
    au2 = AppUser.builder().uid("234").email("au2@daily.it").build();
    au3 = AppUser.builder().uid("123").email("au3@daily.it").build();
  }

  @AfterEach
  void tearDown() {}

  @Test
  void givenDifferentUidAppUsers_whenAddAppUsersToHashMap_thenDiffer() {
    Map<AppUser, String> map = new HashMap<>();
    map.put(au1, "au1");
    map.put(au2, "au2");
    assertEquals("au1", map.get(au1));
    assertEquals("au2", map.get(au2));
  }

  @Test
  void givenEqualUidAppUsers_whenAddAppUsersToHashMap_thenEqual() {
    Map<AppUser, String> map = new HashMap<>();
    map.put(au1, "au1");
    map.put(au3, "au3");
    assertNotEquals("au1", map.get(au1));
    assertEquals("au3", map.get(au1));
  }

  @Test
  void givenDifferentUidAppUsers_whenEquals_thenDiffer() {
    assertNotEquals(au1, au2);
  }

  @Test
  void givenEqualUidAppUsers_whenEquals_thenEqual() {
    assertEquals(au1, au3);
  }
}
