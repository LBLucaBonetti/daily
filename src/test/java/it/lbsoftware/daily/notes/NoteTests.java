package it.lbsoftware.daily.notes;

import static it.lbsoftware.daily.notes.NoteTestUtils.createNote;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NoteTests extends DailyAbstractUnitTests {

  private static final UUID APP_USER = UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final String TEXT = "text";

  @Test
  @DisplayName("Should equal with itself")
  void test1() {
    // Given
    Note note = createNote(TEXT, Collections.emptySet(), APP_USER);

    // When
    boolean res = note.equals(note);

    // Then
    assertTrue(res);
  }

  @Test
  @DisplayName("Should not equal with different object")
  void test2() {
    // Given
    Note note = createNote(TEXT, Collections.emptySet(), APP_USER);

    // When
    boolean res = note.equals("");

    // Then
    assertFalse(res);
  }

  @Test
  @DisplayName("Should not equal when no id is present")
  void test3() {
    // Given
    Note note1 = createNote(TEXT, Collections.emptySet(), APP_USER);
    Note note2 = createNote(TEXT, Collections.emptySet(), APP_USER);

    // When
    boolean res = note1.equals(note2);

    // Then
    assertFalse(res);
  }
}
