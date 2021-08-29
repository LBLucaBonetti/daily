package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.entities.BaseDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class NoteDto extends BaseDto {

    @NotBlank
    private String text;

}
