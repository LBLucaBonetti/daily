package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.bases.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class NoteDto extends BaseDto {

    @NotBlank
    private String text;

}
