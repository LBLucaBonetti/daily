package it.lbsoftware.daily.tags;

import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Mapper for {@link Tag} entities. */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagDtoMapper {

  Tag convertToEntity(TagDto tagDto);

  TagDto convertToDto(Tag tag);

  Set<TagDto> convertToDto(Set<Tag> tags);
}
