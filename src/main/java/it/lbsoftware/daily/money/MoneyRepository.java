package it.lbsoftware.daily.money;

import it.lbsoftware.daily.appusers.AppUser;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Main {@link Money} repository. */
public interface MoneyRepository extends JpaRepository<Money, Long> {

  /**
   * Finds money by period and their {@link AppUser}.
   *
   * @param pageable Pagination and sorting object
   * @param operationDateFrom The starting date to search money operations by
   * @param operationDateTo The ending date to search money operations by
   * @param appUser The appUser
   * @return Found money or empty page
   */
  Page<Money> findByOperationDateBetweenAndAppUser(
      Pageable pageable, LocalDate operationDateFrom, LocalDate operationDateTo, AppUser appUser);

  /**
   * Finds money by its uuid and {@link AppUser}.
   *
   * @param uuid Money uuid
   * @param appUser The appUser
   * @return Found money or empty value
   */
  Optional<Money> findByUuidAndAppUser(UUID uuid, AppUser appUser);

  /**
   * Finds money by its uuid and {@link AppUser} fetching the associated tags.
   *
   * @param uuid Money uuid
   * @param appUser Unique id of the appUser
   * @return Found money or empty value
   */
  @Query(
      "select m from Money m left join fetch m.tags where m.uuid = :uuid and m.appUser = :appUser")
  Optional<Money> findByUuidAndAppUserFetchTags(
      @Param("uuid") UUID uuid, @Param("appUser") AppUser appUser);
}
