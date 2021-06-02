package it.lbsoftware.daily.tags;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@Setter
public class TagDto {

    private UUID uuid;
    @NotBlank
    private String name;
    @NotBlank
    private String colorHex;

}
