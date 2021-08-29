package it.lbsoftware.daily.notes;

import org.mapstruct.Mapper;

@Mapper
public interface NoteDtoMapper {

    Note convertToEntity(NoteDto noteDto);

    NoteDto convertToDto(Note note);

}
