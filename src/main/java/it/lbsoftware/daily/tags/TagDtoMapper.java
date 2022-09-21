package it.lbsoftware.daily.tags;

import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper
public interface TagDtoMapper {

  Tag convertToEntity(TagDto tagDto);

  TagDto convertToDto(Tag tag);

  List<TagDto> convertToDto(List<Tag> tags);

  Set<TagDto> convertToDto(Set<Tag> tags);
}
