package it.lbsoftware.daily.notes;

import org.mapstruct.Mapper;

/** Mapper for {@link Note} entities. */
@Mapper
public interface NoteDtoMapper {

  Note convertToEntity(NoteDto noteDto);

  NoteDto convertToDto(Note note);
}
