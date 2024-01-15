package it.lbsoftware.daily.bases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

class PageDtoTests extends DailyAbstractUnitTests {

  @Test
  @DisplayName("Should throw when create page dto with null page")
  void test1() {
    // Given
    Page<Object> page = null;

    // When
    IllegalArgumentException res =
        assertThrows(IllegalArgumentException.class, () -> new PageDto<>(page));

    // Then
    assertNotNull(res);
  }
}
