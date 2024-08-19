package it.lbsoftware.daily.money;

import it.lbsoftware.daily.appusers.AppUser;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/** Main {@link Money} service implementation. */
@Service
@RequiredArgsConstructor
public class MoneyService {

  private final MoneyRepository moneyRepository;

  /**
   * Reads money.
   *
   * @param pageable Pagination and sorting object
   * @param from Money should be read starting from this operation date
   * @param appUser The owner
   * @return Read money or empty page
   */
  public Page<MoneyDto> readMoney(Pageable pageable, LocalDate from, AppUser appUser) {
    return null;
  }
}
