package it.lbsoftware.daily.notes;

import it.lbsoftware.daily.entities.BaseDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class NoteDtoIn extends BaseDto {

    @NotBlank
    private String text;
    private Set<UUID> tagSet = new HashSet<>();

}
