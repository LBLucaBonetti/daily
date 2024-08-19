package it.lbsoftware.daily.money;

import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
      Pageable pageable, @RequestParam LocalDate from, @AuthenticationPrincipal Object principal) {
    log.info("GET request to /api/money with paging; parameters: %s".formatted(from));
    Page<MoneyDto> readMoney;
    try {
      readMoney = moneyService.readMoney(pageable, from, appUserService.getAppUser(principal));
    } catch (Exception e) {
      log.error(e);
      throw new DailyBadRequestException(null);
    }
    PageDto<MoneyDto> readMoneyDtos = new PageDto<>(readMoney);

    return ResponseEntity.ok(readMoneyDtos);
  }
}
