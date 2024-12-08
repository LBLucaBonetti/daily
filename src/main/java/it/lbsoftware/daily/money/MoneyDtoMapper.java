package it.lbsoftware.daily.money;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Mapper for {@link Money} entities. */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MoneyDtoMapper {

  Money convertToEntity(MoneyDto moneyDto);

  MoneyDto convertToDto(Money money);
}
