package it.lbsoftware.daily.appusersettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUserDto;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class AppUserSettingUtilsTests extends DailyAbstractUnitTests {

  @Test
  @DisplayName("Should not instantiate the class")
  void test1() throws NoSuchMethodException {
    // Given
    Constructor<AppUserSettingUtils> utils = AppUserSettingUtils.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(utils.getModifiers()));
    utils.setAccessible(true);

    // When
    InvocationTargetException res =
        assertThrows(InvocationTargetException.class, utils::newInstance);

    // Then
    assertNotNull(res);
    assertInstanceOf(UnsupportedOperationException.class, res.getCause());
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when get app user settings with null argument")
  void test2(AppUserDto appUserDto) {
    assertThrows(
        IllegalArgumentException.class, () -> AppUserSettingUtils.getAppUserSettings(appUserDto));
  }

  @Test
  @DisplayName("Should get app user settings with the same app user lang")
  void test3() {
    // Given
    String lang = "en-US";
    AppUserDto appUserDto = new AppUserDto();
    appUserDto.setLang(lang);

    // When
    var res = AppUserSettingUtils.getAppUserSettings(appUserDto);

    // Then
    assertEquals(lang, res.getLang());
  }
}
