package it.lbsoftware.daily.notes;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface NoteDtoMapper {

  Note convertToEntity(NoteDto noteDto);

  NoteDto convertToDto(Note note);

  List<NoteDto> convertToDto(List<Note> notes);
}
