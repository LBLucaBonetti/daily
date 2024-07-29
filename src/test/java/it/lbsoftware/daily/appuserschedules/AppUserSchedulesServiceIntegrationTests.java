package it.lbsoftware.daily.appuserschedules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import it.lbsoftware.daily.appusers.AppUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("App user schedules service integration tests")
class AppUserSchedulesServiceIntegrationTests extends DailyAbstractIntegrationTests {

  @Autowired private AppUserRepository appUserRepository;
  @Autowired private AppUserSchedulesService appUserSchedulesService;

  @Test
  @Sql("/add-appusers-to-notify-for-removal.sql")
  @DisplayName("Should notify without removing old app users")
  void test1() {
    // Given
    assertEquals(4, (long) appUserRepository.findAll().size());
    assertEmailMessageCount(0);

    // When
    appUserSchedulesService.execute();

    // Then
    assertEmailMessageCount(4);
    assertEquals(4, (long) appUserRepository.findAll().size());
  }

  @Test
  @Sql("/add-appusers-to-remove.sql")
  @DisplayName("Should remove without notifying old app users")
  void test2() {
    // Given
    assertEquals(4, (long) appUserRepository.findAll().size());
    assertEmailMessageCount(0);

    // When
    appUserSchedulesService.execute();

    // Then
    assertEmailMessageCount(0);
    assertEquals(0, (long) appUserRepository.findAll().size());
  }

  @Test
  @Sql("/add-appusers-not-to-be-removed.sql")
  @DisplayName("Should not remove not-to-be-removed app users")
  void test3() {
    // Given
    assertEquals(3, (long) appUserRepository.findAll().size());
    assertEmailMessageCount(0);

    // When
    appUserSchedulesService.execute();

    // Then
    assertEquals(3, (long) appUserRepository.findAll().size());
  }
}
