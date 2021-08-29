package it.lbsoftware.daily.tags;

import org.mapstruct.Mapper;

@Mapper
public interface TagDtoMapper {

    Tag convertToEntity(TagDto tagDto);

    TagDto convertToDto(Tag tag);

}
