package it.lbsoftware.daily.money;

import static it.lbsoftware.daily.TestUtils.loginOf;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.APP_USER_FULLNAME;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.OTHER_APP_USER_EMAIL;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.OTHER_APP_USER_FULLNAME;
import static it.lbsoftware.daily.appusers.AppUserTestUtils.saveOauth2OtherAppUser;
import static it.lbsoftware.daily.money.MoneyTestUtils.createMoney;
import static it.lbsoftware.daily.money.MoneyTestUtils.createMoneyDto;
import static it.lbsoftware.daily.tags.TagTestUtils.createTag;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.lbsoftware.daily.DailyAbstractIntegrationTests;
import it.lbsoftware.daily.appusers.AppUserRepository;
import it.lbsoftware.daily.appusers.AppUserTestUtils;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
import it.lbsoftware.daily.money.Money.OperationType;
import it.lbsoftware.daily.tags.TagRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
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
  private static final BigDecimal OTHER_AMOUNT = BigDecimal.ONE;
  private static final OperationType OPERATION_TYPE = OperationType.INCOME;
  private static final OperationType OTHER_OPERATION_TYPE = OperationType.OUTCOME;
  private static final String DESCRIPTION = "description";
  private static final String OTHER_DESCRIPTION = "other description";
  private static final String NAME = "name";
  private static final String COLOR_HEX = "#123456";
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MoneyRepository moneyRepository;
  @Autowired private MoneyDtoMapper moneyDtoMapper;
  @Autowired private AppUserRepository appUserRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private TagRepository tagRepository;

  private static Stream<MoneyDto> test5() {
    var nullOperationDateMoneyDto = new MoneyDto();
    nullOperationDateMoneyDto.setOperationDate(null);
    nullOperationDateMoneyDto.setAmount(OTHER_AMOUNT);
    nullOperationDateMoneyDto.setOperationType(OTHER_OPERATION_TYPE);
    nullOperationDateMoneyDto.setDescription(OTHER_DESCRIPTION);
    var nullAmountMoneyDto = new MoneyDto();
    nullAmountMoneyDto.setOperationDate(OTHER_OPERATION_DATE);
    nullAmountMoneyDto.setAmount(null);
    nullAmountMoneyDto.setOperationType(OTHER_OPERATION_TYPE);
    nullAmountMoneyDto.setDescription(OTHER_DESCRIPTION);
    var nullOperationTypeMoneyDto = new MoneyDto();
    nullOperationTypeMoneyDto.setOperationDate(OTHER_OPERATION_DATE);
    nullOperationTypeMoneyDto.setAmount(OTHER_AMOUNT);
    nullOperationTypeMoneyDto.setOperationType(null);
    nullOperationTypeMoneyDto.setDescription(OTHER_DESCRIPTION);
    var tooLongDescriptionMoneyDto = new MoneyDto();
    tooLongDescriptionMoneyDto.setOperationDate(OTHER_OPERATION_DATE);
    tooLongDescriptionMoneyDto.setAmount(OTHER_AMOUNT);
    tooLongDescriptionMoneyDto.setOperationType(OTHER_OPERATION_TYPE);
    var tooLongDescription =
        "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678"
            + "1234567812345678";
    tooLongDescriptionMoneyDto.setDescription(tooLongDescription);
    return Stream.of(
        nullOperationDateMoneyDto,
        nullAmountMoneyDto,
        nullOperationTypeMoneyDto,
        tooLongDescriptionMoneyDto);
  }

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

  @ParameterizedTest
  @NullSource
  @MethodSource
  @DisplayName("Should return bad request when update money with wrong arguments")
  void test5(final MoneyDto moneyDto) throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var money =
        moneyRepository.save(
            createMoney(
                OPERATION_DATE,
                AMOUNT,
                OPERATION_TYPE,
                DESCRIPTION,
                Collections.emptySet(),
                appUser));

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", money.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moneyDto))
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());

    // Then
    money = moneyRepository.findByUuidAndAppUserFetchTags(money.getUuid(), appUser).get();
    assertEquals(OPERATION_DATE, money.getOperationDate());
    assertEquals(AMOUNT, money.getAmount());
    assertEquals(OPERATION_TYPE, money.getOperationType());
    assertEquals(DESCRIPTION, money.getDescription());
    assertEquals(Collections.emptySet(), money.getTags());
    assertEquals(appUser, money.getAppUser());
  }

  @Test
  @DisplayName("Should return bad request when update money with wrong uuid")
  void test6() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    String uuid = "not-a-uuid";

    // When and then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when update money of another app user")
  void test7() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    final var otherAppUser = saveOauth2OtherAppUser(appUserRepository, passwordEncoder);
    var money =
        moneyRepository.save(
            createMoney(
                OPERATION_DATE,
                AMOUNT,
                OPERATION_TYPE,
                DESCRIPTION,
                Collections.emptySet(),
                appUser));
    var moneyDto =
        createMoneyDto(
            null, OTHER_OPERATION_DATE, OTHER_AMOUNT, OTHER_OPERATION_TYPE, OTHER_DESCRIPTION);

    // When and then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}", money.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moneyDto))
                .with(csrf())
                .with(
                    loginOf(otherAppUser.getUuid(), OTHER_APP_USER_FULLNAME, OTHER_APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should update money")
  void test8() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var money =
        moneyRepository.save(
            createMoney(
                OPERATION_DATE,
                AMOUNT,
                OPERATION_TYPE,
                DESCRIPTION,
                Collections.emptySet(),
                appUser));
    var moneyDto =
        createMoneyDto(
            null, OTHER_OPERATION_DATE, OTHER_AMOUNT, OTHER_OPERATION_TYPE, OTHER_DESCRIPTION);

    // When
    var res =
        objectMapper.readValue(
            mockMvc
                .perform(
                    put(BASE_URL + "/{uuid}", money.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moneyDto))
                        .with(csrf())
                        .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MoneyDto.class);

    // Then
    assertEquals(OTHER_OPERATION_DATE, res.getOperationDate());
    assertEquals(OTHER_AMOUNT, res.getAmount());
    assertEquals(OTHER_OPERATION_TYPE, res.getOperationType());
    assertEquals(OTHER_DESCRIPTION, res.getDescription());
  }

  @Test
  @DisplayName("Should return unauthorized when update money, csrf and no auth")
  void test9() throws Exception {
    mockMvc
        .perform(put(BASE_URL + "/{uuid}", UUID.randomUUID()).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return forbidden when update money, no csrf and no auth")
  void test10() throws Exception {
    mockMvc.perform(put(BASE_URL + "/{uuid}", UUID.randomUUID())).andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return unauthorized when delete money, csrf and no auth")
  void test11() throws Exception {
    mockMvc
        .perform(delete(BASE_URL + "/{uuid}", UUID.randomUUID()).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return forbidden when delete money, no csrf and no auth")
  void test12() throws Exception {
    mockMvc
        .perform(delete(BASE_URL + "/{uuid}", UUID.randomUUID()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return bad request when delete money with wrong uuid")
  void test13() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid = "not-a-uuid";

    // When and then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when delete money of another app user")
  void test14() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    final var otherAppUser = saveOauth2OtherAppUser(appUserRepository, passwordEncoder);
    var money =
        moneyRepository.save(
            createMoney(
                OPERATION_DATE,
                AMOUNT,
                OPERATION_TYPE,
                DESCRIPTION,
                Collections.emptySet(),
                appUser));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", money.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(
                    loginOf(otherAppUser.getUuid(), OTHER_APP_USER_FULLNAME, OTHER_APP_USER_EMAIL)))
        .andExpect(status().isNotFound());

    // Then
    assertEquals(1, moneyRepository.count());
  }

  @Test
  @DisplayName("Should return not found when delete money and it does not exist")
  void test15() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid = UUID.randomUUID();

    // When and then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", uuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should not delete tag and should remove money from tag money when delete money")
  void test16() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var money =
        moneyRepository.save(
            createMoney(
                OPERATION_DATE, AMOUNT, OPERATION_TYPE, DESCRIPTION, new HashSet<>(), appUser));
    var tag = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), appUser));
    tag.addToMoney(money);
    moneyRepository.save(money);
    assertTrue(money.getTags().contains(tag));
    assertTrue(tag.getMoney().contains(money));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", money.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(0, moneyRepository.count());
    assertEquals(1, tagRepository.count());
    assertTrue(
        tagRepository
            .findByUuidAndAppUserFetchMoney(tag.getUuid(), appUser)
            .get()
            .getMoney()
            .isEmpty());
  }

  @Test
  @DisplayName("Should delete money")
  void test17() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var money =
        moneyRepository.save(
            createMoney(
                OPERATION_DATE,
                AMOUNT,
                OPERATION_TYPE,
                DESCRIPTION,
                Collections.emptySet(),
                appUser));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}", money.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNoContent());

    // Then
    assertEquals(0, moneyRepository.count());
  }

  @Test
  @DisplayName("Should return unauthorized when add tag to money, csrf and no auth")
  void test18() throws Exception {
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), UUID.randomUUID())
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return forbidden when add tag to money, no csrf and no auth")
  void test19() throws Exception {
    mockMvc
        .perform(put(BASE_URL + "/{uuid}/tags/{tagsUuid}", UUID.randomUUID(), UUID.randomUUID()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return bad request when add tag to money with wrong uuid")
  void test20() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid = "not-a-uuid";

    // When and then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when add tag to money with wrong tagUuid")
  void test21() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var tagUuid = "not-a-uuid";

    // When and then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when add tag to money with wrong uuid and tagUuid")
  void test22() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid = "not-a-uuid";
    var tagUuid = "not-a-uuid";

    // When and then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when add tag to money and money does not exist")
  void test23() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid = UUID.randomUUID();

    // When and then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when add tag to money and tag does not exist")
  void test24() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid =
        moneyRepository
            .save(
                createMoney(
                    OPERATION_DATE,
                    AMOUNT,
                    OPERATION_TYPE,
                    DESCRIPTION,
                    Collections.emptySet(),
                    appUser))
            .getUuid();
    var tagUuid = UUID.randomUUID();

    // When and then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when add tag to money and money is of another app user")
  void test25() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    final var otherAppUser = saveOauth2OtherAppUser(appUserRepository, passwordEncoder);
    var uuid =
        moneyRepository
            .save(
                createMoney(
                    OPERATION_DATE,
                    AMOUNT,
                    OPERATION_TYPE,
                    DESCRIPTION,
                    Collections.emptySet(),
                    otherAppUser))
            .getUuid();

    // When and then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when add tag to money and tag is of another app user")
  void test26() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    final var otherAppUser = saveOauth2OtherAppUser(appUserRepository, passwordEncoder);
    var uuid =
        moneyRepository
            .save(
                createMoney(
                    OPERATION_DATE,
                    AMOUNT,
                    OPERATION_TYPE,
                    DESCRIPTION,
                    Collections.emptySet(),
                    appUser))
            .getUuid();
    var tagUuid =
        tagRepository
            .save(createTag(NAME, COLOR_HEX, Collections.emptySet(), otherAppUser))
            .getUuid();

    // When and then
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should add tag to money")
  void test27() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid =
        moneyRepository
            .save(
                createMoney(
                    OPERATION_DATE, AMOUNT, OPERATION_TYPE, DESCRIPTION, new HashSet<>(), appUser))
            .getUuid();
    var tagUuid =
        tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), appUser)).getUuid();

    // When
    mockMvc
        .perform(
            put(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNoContent());

    // Then
    var money = moneyRepository.findByUuidAndAppUserFetchTags(uuid, appUser).get();
    var tag = tagRepository.findByUuidAndAppUserFetchMoney(tagUuid, appUser).get();
    assertEquals(1, money.getTags().size());
    assertTrue(money.getTags().contains(tag));
    assertEquals(1, tag.getMoney().size());
    assertTrue(tag.getMoney().contains(money));
  }

  @Test
  @DisplayName("Should return unauthorized when remove tag from money, csrf and no auth")
  void test28() throws Exception {
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), UUID.randomUUID())
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return forbidden when remove tag from money, no csrf and no auth")
  void test29() throws Exception {
    mockMvc
        .perform(delete(BASE_URL + "/{uuid}/tags/{tagsUuid}", UUID.randomUUID(), UUID.randomUUID()))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return bad request when remove tag from money with wrong uuid")
  void test30() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid = "not-a-uuid";

    // When and then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when remove tag from money with wrong tagUuid")
  void test31() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var tagUuid = "not-a-uuid";

    // When and then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", UUID.randomUUID(), tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return bad request when remove tag from money with wrong uuid and tagUuid")
  void test32() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid = "not-a-uuid";
    var tagUuid = "not-a-uuid";

    // When and then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return not found when remove tag from money and money does not exist")
  void test33() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid = UUID.randomUUID();

    // When and then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when remove tag from money and tag does not exist")
  void test34() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var uuid =
        moneyRepository
            .save(
                createMoney(
                    OPERATION_DATE,
                    AMOUNT,
                    OPERATION_TYPE,
                    DESCRIPTION,
                    Collections.emptySet(),
                    appUser))
            .getUuid();
    var tagUuid = UUID.randomUUID();

    // When and then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName(
      "Should return not found when remove tag from money and money is of another app user")
  void test35() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    final var otherAppUser = saveOauth2OtherAppUser(appUserRepository, passwordEncoder);
    var uuid =
        moneyRepository
            .save(
                createMoney(
                    OPERATION_DATE,
                    AMOUNT,
                    OPERATION_TYPE,
                    DESCRIPTION,
                    Collections.emptySet(),
                    otherAppUser))
            .getUuid();

    // When and then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return not found when remove tag from money and tag is of another app user")
  void test36() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    final var otherAppUser = saveOauth2OtherAppUser(appUserRepository, passwordEncoder);
    var uuid =
        moneyRepository
            .save(
                createMoney(
                    OPERATION_DATE,
                    AMOUNT,
                    OPERATION_TYPE,
                    DESCRIPTION,
                    Collections.emptySet(),
                    appUser))
            .getUuid();
    var tagUuid =
        tagRepository
            .save(createTag(NAME, COLOR_HEX, Collections.emptySet(), otherAppUser))
            .getUuid();

    // When and then
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", uuid, tagUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should remove tag from money")
  void test37() throws Exception {
    // Given
    final var appUser = AppUserTestUtils.saveOauth2AppUser(appUserRepository, passwordEncoder);
    var money =
        moneyRepository.save(
            createMoney(
                OPERATION_DATE, AMOUNT, OPERATION_TYPE, DESCRIPTION, new HashSet<>(), appUser));
    var tag = tagRepository.save(createTag(NAME, COLOR_HEX, new HashSet<>(), appUser));
    tag.addToMoney(money);
    moneyRepository.save(money);
    assertTrue(money.getTags().contains(tag));
    assertTrue(tag.getMoney().contains(money));

    // When
    mockMvc
        .perform(
            delete(BASE_URL + "/{uuid}/tags/{tagUuid}", money.getUuid(), tag.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(loginOf(appUser.getUuid(), APP_USER_FULLNAME, APP_USER_EMAIL)))
        .andExpect(status().isNoContent());

    // Then
    money = moneyRepository.findByUuidAndAppUserFetchTags(money.getUuid(), appUser).get();
    tag = tagRepository.findByUuidAndAppUserFetchMoney(tag.getUuid(), appUser).get();
    assertEquals(0, money.getTags().size());
    assertFalse(money.getTags().contains(tag));
    assertEquals(0, tag.getMoney().size());
    assertFalse(tag.getMoney().contains(money));
  }
}
