package it.lbsoftware.daily.money;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_FULLNAME;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.OTHER_APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.OTHER_APP_USER_FULLNAME;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.saveOauth2OtherAppUser;
import static it.lbsoftware.daily.money.MoneyTestUtils.createMoney;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserTestUtils;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
import it.lbsoftware.daily.money.Money.OperationType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("Money integration tests")
class MoneyIntegrationTests extends DailyAbstractIntegrationTests {

  private static final String BASE_URL = "/api/money";
  private static final LocalDate OPERATION_DATE = LocalDate.now();
  private static final LocalDate OTHER_OPERATION_DATE = LocalDate.now().minusDays(1);
  private static final BigDecimal AMOUNT = BigDecimal.TEN;
  private static final OperationType OPERATION_TYPE = OperationType.INCOME;
  private static final String DESCRIPTION = "description";
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MoneyRepository moneyRepository;
  @Autowired private MoneyDtoMapper moneyDtoMapper;
  @Autowired private AppUserRepository appUserRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  void beforeEach() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @DisplayName("Should return unauthorized when read money and no auth")
  void test1() throws Exception {
    mockMvc
        .perform(get(BASE_URL).queryParam("year-month-date", "2023-12-31"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return empty list when read money of another app user")
  void test2() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    final var otherAppUser = saveOauth2OtherAppUser(appUserRepository, passwordEncoder);
    moneyRepository.save(
        createMoney(
            OPERATION_DATE, AMOUNT, OPERATION_TYPE, DESCRIPTION, Collections.emptySet(), appUser));
    moneyRepository.save(
        createMoney(
            OTHER_OPERATION_DATE,
            AMOUNT,
            OPERATION_TYPE,
            DESCRIPTION,
            Collections.emptySet(),
            appUser));

    // When
    PageDto<MoneyDto> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL)
                        .queryParam("year-month-date", "2023-12-31")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(
                            loginOf(
                                otherAppUser.getUuid(),
                                OTHER_APP_USER_FULLNAME,
                                OTHER_APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    List<MoneyDto> moneyDtos = res.getContent();
    assertTrue(moneyDtos.isEmpty());
  }

  @Test
  @DisplayName(
      "Should not read money because of wrong money field name as sort parameter and return bad request")
  void test3() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    String nonexistentField = "nonexistent-field";

    // When
    var res =
        mockMvc
            .perform(
                get(BASE_URL)
                    .queryParam("year-month-date", "2023-12-31")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("sort", nonexistentField)
                    .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResolvedException();

    // Then
    assertInstanceOf(DailyBadRequestException.class, res);
    assertNull(res.getMessage());
  }

  @Test
  @DisplayName("Should read money")
  void test4() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var operationDate1 = LocalDate.of(1993, 5, 1);
    var operationDate2 = LocalDate.of(1993, 5, 31);
    var operationDate3 = LocalDate.of(1993, 4, 30);
    var operationDate4 = LocalDate.of(1993, 6, 1);
    var yearMonthDate = LocalDate.of(1993, 5, 17);
    MoneyDto moneyDto1 =
        moneyDtoMapper.convertToDto(
            moneyRepository.save(
                createMoney(
                    operationDate1,
                    AMOUNT,
                    OPERATION_TYPE,
                    DESCRIPTION,
                    Collections.emptySet(),
                    appUser)));
    MoneyDto moneyDto2 =
        moneyDtoMapper.convertToDto(
            moneyRepository.save(
                createMoney(
                    operationDate2,
                    AMOUNT,
                    OPERATION_TYPE,
                    DESCRIPTION,
                    Collections.emptySet(),
                    appUser)));
    MoneyDto moneyDto3 =
        moneyDtoMapper.convertToDto(
            moneyRepository.save(
                createMoney(
                    operationDate3,
                    AMOUNT,
                    OPERATION_TYPE,
                    DESCRIPTION,
                    Collections.emptySet(),
                    appUser)));
    MoneyDto moneyDto4 =
        moneyDtoMapper.convertToDto(
            moneyRepository.save(
                createMoney(
                    operationDate4,
                    AMOUNT,
                    OPERATION_TYPE,
                    DESCRIPTION,
                    Collections.emptySet(),
                    appUser)));

    // When
    PageDto<MoneyDto> res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    get(BASE_URL)
                        .queryParam("year-month-date", yearMonthDate.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            new TypeReference<>() {});

    // Then
    List<MoneyDto> moneyDtos = res.getContent();
    assertFalse(moneyDtos.isEmpty());
    assertEquals(2, moneyDtos.size());
    assertThat(moneyDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(moneyDto1);
    assertThat(moneyDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt")
        .contains(moneyDto2);
    assertThat(moneyDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAd", "updatedAt")
        .doesNotContain(moneyDto3);
    assertThat(moneyDtos)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAd", "updatedAt")
        .doesNotContain(moneyDto4);
  }
}
