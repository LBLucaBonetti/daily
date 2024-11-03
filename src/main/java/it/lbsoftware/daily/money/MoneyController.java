package it.lbsoftware.daily.money;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
import it.lbsoftware.daily.tags.TagDto;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/money")
@CommonsLog
class MoneyController {

  private final MoneyService moneyService;
  private final AppUserService appUserService;

  @GetMapping
  public ResponseEntity<PageDto<MoneyDto>> readMoney(
      Pageable pageable,
      @RequestParam(name = "year-month-date") LocalDate yearMonthDate,
      @AuthenticationPrincipal Object principal) {
    log.info("GET request to /api/money with paging; parameters: %s".formatted(yearMonthDate));
    var from = yearMonthDate.with(firstDayOfMonth());
    var to = yearMonthDate.with(lastDayOfMonth());
    Page<MoneyDto> readMoney;
    try {
      readMoney = moneyService.readMoney(pageable, from, to, appUserService.getAppUser(principal));
    } catch (Exception e) {
      log.error(e);
      throw new DailyBadRequestException(null);
    }
    PageDto<MoneyDto> readMoneyDtos = new PageDto<>(readMoney);

    return ResponseEntity.ok(readMoneyDtos);
  }

  @PutMapping(value = "/{uuid}")
  public ResponseEntity<MoneyDto> updateMoney(
      @PathVariable("uuid") UUID uuid,
      @Valid @RequestBody MoneyDto moneyDto,
      @AuthenticationPrincipal Object principal) {
    log.info(
        "PUT request to /api/money/%s; parameters: %s"
            .formatted(uuid.toString(), moneyDto.toString()));
    return moneyService
        .updateMoney(uuid, moneyDto, appUserService.getAppUser(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping(value = "/{uuid}")
  public ResponseEntity<MoneyDto> deleteMoney(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal Object principal) {
    log.info("DELETE request to /api/money/%s".formatted(uuid.toString()));
    moneyService.deleteMoney(uuid, appUserService.getAppUser(principal));

    return ResponseEntity.noContent().build();
  }

  @PutMapping(value = "/{uuid}/tags/{tagUuid}")
  public ResponseEntity<TagDto> addTagToMoney(
      @PathVariable("uuid") UUID uuid,
      @PathVariable("tagUuid") UUID tagUuid,
      @AuthenticationPrincipal Object principal) {
    log.info("PUT request to /api/money/%s/tags/%s".formatted(uuid.toString(), tagUuid.toString()));
    moneyService.addTagToMoney(uuid, tagUuid, appUserService.getAppUser(principal));

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/{uuid}/tags/{tagUuid}")
  public ResponseEntity<TagDto> removeTagFromMoney(
      @PathVariable("uuid") UUID uuid,
      @PathVariable("tagUuid") UUID tagUuid,
      @AuthenticationPrincipal Object principal) {
    log.info(
        "DELETE request to /api/money/%s/tags/%s".formatted(uuid.toString(), tagUuid.toString()));
    moneyService.removeTagFromMoney(uuid, tagUuid, appUserService.getAppUser(principal));

    return ResponseEntity.noContent().build();
  }

  @PostMapping
  public ResponseEntity<MoneyDto> createMoney(
      @Valid @RequestBody MoneyDto moneyDto, @AuthenticationPrincipal Object principal) {
    log.info("POST request to /api/money; parameters: %s".formatted(moneyDto.toString()));
    var createdMoneyDto = moneyService.createMoney(moneyDto, appUserService.getAppUser(principal));

    return ResponseEntity.status(HttpStatus.CREATED).body(createdMoneyDto);
  }
}
