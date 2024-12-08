package it.lbsoftware.daily.notes;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Mapper for {@link Note} entities. */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteDtoMapper {

  Note convertToEntity(NoteDto noteDto);

  NoteDto convertToDto(Note note);
}
