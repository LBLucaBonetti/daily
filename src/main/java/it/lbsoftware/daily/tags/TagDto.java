package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.entities.BaseDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TagDto extends BaseDto {

    @NotBlank
    private String name;
    @NotBlank
    private String colorHex;

}
