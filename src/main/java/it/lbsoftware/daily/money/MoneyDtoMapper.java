package it.lbsoftware.daily.money;

import org.mapstruct.Mapper;

/** Mapper for {@link Money} entities. */
@Mapper
public interface MoneyDtoMapper {

  Money convertToEntity(MoneyDto moneyDto);

  MoneyDto convertToDto(Money money);
}
