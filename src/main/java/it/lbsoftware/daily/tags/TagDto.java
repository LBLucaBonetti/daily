package it.lbsoftware.daily.tags;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TagDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String colorHex;

}
