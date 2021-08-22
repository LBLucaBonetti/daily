package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.entities.BaseDto;
import it.lbsoftware.daily.tags.TagDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class NoteDtoOut extends BaseDto {

    @NotBlank
    private String text;
    private Set<TagDto> tagSet = new HashSet<>();

}
