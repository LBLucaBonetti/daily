package it.lbsoftware.daily.tags;

import java.util.Set;
import org.mapstruct.Mapper;

/** Mapper for {@link Tag} entities. */
@Mapper
public interface TagDtoMapper {

  Tag convertToEntity(TagDto tagDto);

  TagDto convertToDto(Tag tag);

  Set<TagDto> convertToDto(Set<Tag> tags);
}
