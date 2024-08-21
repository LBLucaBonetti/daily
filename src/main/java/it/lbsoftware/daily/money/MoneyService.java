package it.lbsoftware.daily.money;

import it.lbsoftware.daily.appusers.AppUser;
import java.time.LocalDate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Main {@link Money} service implementation. */
@Service
@RequiredArgsConstructor
public class MoneyService {

  private final MoneyRepository moneyRepository;
  private final MoneyDtoMapper moneyDtoMapper;

  /**
   * Reads money of the period indicated by the given dates
   *
   * @param pageable Pagination and sorting object
   * @param from The starting date to search money operations by
   * @param to The ending date to search money operations by
   * @param appUser The owner
   * @return Read money or empty page
   */
  @Transactional(readOnly = true)
  public Page<MoneyDto> readMoney(
      Pageable pageable, @NonNull LocalDate from, @NonNull LocalDate to, @NonNull AppUser appUser) {
    return moneyRepository
        .findByOperationDateBetweenAndAppUser(pageable, from, to, appUser)
        .map(moneyDtoMapper::convertToDto);
  }
}
