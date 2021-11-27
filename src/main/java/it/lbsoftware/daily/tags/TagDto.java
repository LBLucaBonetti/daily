package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.bases.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TagDto extends BaseDto {

    @NotBlank
    private String name;
    @NotNull
    @Pattern(regexp = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$")
    private String colorHex;

}
