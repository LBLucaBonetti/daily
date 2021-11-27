package it.lbsoftware.daily.bases;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class BaseDto {

    private UUID uuid;

}