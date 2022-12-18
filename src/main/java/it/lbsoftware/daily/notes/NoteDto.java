package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.bases.BaseDto;
import it.lbsoftware.daily.config.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class NoteDto extends BaseDto {

  @NotBlank
  @Size(max = Constants.NOTE_TEXT_MAX)
  private String text;
}
