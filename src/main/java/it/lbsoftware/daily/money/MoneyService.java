package it.lbsoftware.daily.money;

import static it.lbsoftware.daily.config.Constants.DO_NOT_STORE_NULL_SPEL;
import static it.lbsoftware.daily.config.Constants.MONEY_CACHE;
import static it.lbsoftware.daily.config.Constants.MONEY_TAGS_CACHE_KEY_SPEL;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.config.Constants;
import it.lbsoftware.daily.exceptions.DailyConflictException;
import it.lbsoftware.daily.exceptions.DailyNotFoundException;
import it.lbsoftware.daily.tags.TagDto;
import it.lbsoftware.daily.tags.TagDtoMapper;
import it.lbsoftware.daily.tags.TagRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Main {@link Money} service implementation. */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = MONEY_CACHE)
public class MoneyService {

  private final MoneyRepository moneyRepository;
  private final MoneyDtoMapper moneyDtoMapper;
  private final TagRepository tagRepository;
  private final TagDtoMapper tagDtoMapper;

  /**
   * Reads money of the period indicated by the given dates.
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

  /**
   * Updates money.
   *
   * @param uuid Money uuid
   * @param money Money object with new data
   * @param appUser The owner
   * @return Updated money or empty value
   */
  @Transactional
  public Optional<MoneyDto> updateMoney(
      @NonNull UUID uuid, @NonNull MoneyDto money, @NonNull AppUser appUser) {
    return moneyRepository
        .findByUuidAndAppUser(uuid, appUser)
        .map(
            prevMoney -> {
              prevMoney.setAmount(money.getAmount());
              prevMoney.setDescription(money.getDescription());
              prevMoney.setOperationType(money.getOperationType());
              prevMoney.setOperationDate(money.getOperationDate());
              return moneyRepository.saveAndFlush(prevMoney);
            })
        .map(moneyDtoMapper::convertToDto);
  }

  /**
   * Deletes money.
   *
   * @param uuid Money uuid
   * @param appUser The owner
   */
  @Transactional
  @CacheEvict(key = MONEY_TAGS_CACHE_KEY_SPEL)
  public void deleteMoney(@NonNull UUID uuid, @NonNull AppUser appUser) {
    var money =
        moneyRepository
            .findByUuidAndAppUser(uuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    moneyRepository.delete(money);
  }

  /**
   * Adds a tag to money.
   *
   * @param uuid Money uuid
   * @param tagUuid Tag uuid
   * @param appUser The owner
   */
  @Transactional
  @CacheEvict(key = MONEY_TAGS_CACHE_KEY_SPEL)
  public void addTagToMoney(@NonNull UUID uuid, @NonNull UUID tagUuid, @NonNull AppUser appUser) {
    var money =
        moneyRepository
            .findByUuidAndAppUser(uuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    var tag =
        tagRepository
            .findByUuidAndAppUser(tagUuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    if (money.getTags().size() >= Constants.MONEY_TAGS_MAX) {
      throw new DailyConflictException(Constants.ERROR_MONEY_TAGS_MAX);
    }
    tag.addToMoney(money);
  }

  /**
   * Removes a tag from money.
   *
   * @param uuid Money uuid
   * @param tagUuid Tag uuid
   * @param appUser The owner
   */
  @Transactional
  @CacheEvict(key = MONEY_TAGS_CACHE_KEY_SPEL)
  public void removeTagFromMoney(
      @NonNull UUID uuid, @NonNull UUID tagUuid, @NonNull AppUser appUser) {
    var money =
        moneyRepository
            .findByUuidAndAppUser(uuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    var tag =
        tagRepository
            .findByUuidAndAppUser(tagUuid, appUser)
            .orElseThrow(() -> new DailyNotFoundException(Constants.ERROR_NOT_FOUND));
    tag.removeFromMoney(money);
  }

  /**
   * Creates money.
   *
   * @param money Money object to be created
   * @param appUser The owner
   * @return Created money
   */
  @Transactional
  public MoneyDto createMoney(@NonNull MoneyDto money, @NonNull AppUser appUser) {
    var moneyEntity = moneyDtoMapper.convertToEntity(money);
    moneyEntity.setAppUser(appUser);
    var savedMoneyEntity = moneyRepository.saveAndFlush(moneyEntity);

    return moneyDtoMapper.convertToDto(savedMoneyEntity);
  }

  /**
   * Reads money tags.
   *
   * @param uuid Money uuid
   * @param appUser The owner
   * @return Read money tags or empty value if the money does not exist
   */
  @Transactional(readOnly = true)
  @Cacheable(key = MONEY_TAGS_CACHE_KEY_SPEL, unless = DO_NOT_STORE_NULL_SPEL)
  public Optional<Set<TagDto>> readMoneyTags(@NonNull UUID uuid, @NonNull AppUser appUser) {
    return moneyRepository
        .findByUuidAndAppUserFetchTags(uuid, appUser)
        .map(Money::getTags)
        .map(tagDtoMapper::convertToDto);
  }
}
