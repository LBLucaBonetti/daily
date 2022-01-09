package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.bases.BaseDto;
import javax.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class NoteDto extends BaseDto {

  @NotBlank private String text;
}
