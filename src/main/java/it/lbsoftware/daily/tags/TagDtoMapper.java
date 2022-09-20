package it.lbsoftware.daily.tags;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface TagDtoMapper {

  Tag convertToEntity(TagDto tagDto);

  TagDto convertToDto(Tag tag);

  List<TagDto> convertToDto(List<Tag> tags);
}
