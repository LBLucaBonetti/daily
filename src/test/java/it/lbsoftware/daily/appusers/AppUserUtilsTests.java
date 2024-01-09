package it.lbsoftware.daily.appusers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import it.lbsoftware.daily.appusers.AppUser.AuthProvider;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class AppUserUtilsTests extends DailyAbstractUnitTests {

  @ParameterizedTest
  @NullSource
  @DisplayName("Should throw when get oauth2 auth provider with null argument")
  void test1(String email) {
    assertThrows(IllegalArgumentException.class, () -> AppUserUtils.getOauth2AuthProvider(email));
  }

  @Test
  @DisplayName("Should get GOOGLE oauth2 auth provider and return it")
  void test2() {
    // Given
    var email = "appuser@gmail.com";

    // When
    var res = AppUserUtils.getOauth2AuthProvider(email);

    // Then
    assertEquals(AuthProvider.GOOGLE, res);
  }

  @Test
  @DisplayName("Should get DAILY auth provider and return it")
  void test3() {
    // Given
    var email = "appuser@email.com";

    // When
    var res = AppUserUtils.getOauth2AuthProvider(email);

    // Then
    assertEquals(AuthProvider.DAILY, res);
  }

  @Test
  @DisplayName("Should not instantiate the class")
  void test4() throws NoSuchMethodException {
    // Given
    Constructor<AppUserUtils> utils = AppUserUtils.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(utils.getModifiers()));
    utils.setAccessible(true);

    // When
    InvocationTargetException res =
        assertThrows(InvocationTargetException.class, utils::newInstance);
    utils.setAccessible(false);

    // Then
    assertNotNull(res);
    assertInstanceOf(UnsupportedOperationException.class, res.getCause());
  }
}
