package it.lbsoftware.daily.tags;

import static it.lbsoftware.daily.appusers.AppUserTestUtils.createAppUser;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TagTests extends DailyAbstractUnitTests {

  private static final AppUser APP_USER = createAppUser(UUID.randomUUID(), "appuser@email.com");
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";

  @Test
  @DisplayName("Should equal with itself")
  void test1() {
    // Given
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);

    // When
    boolean res = tag.equals(tag);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should not equal with different object")
  void test2() {
    // Given
    Tag tag = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);

    // When
    boolean res = tag.equals("");

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should not equal when no id is present")
  void test3() {
    // Given
    Tag tag1 = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);
    Tag tag2 = createTag(NAME, COLOR_HEX, Collections.emptySet(), APP_USER);

    // When
    boolean res = tag1.equals(tag2);

    // Then
    assertFalse(res);
  }
}
