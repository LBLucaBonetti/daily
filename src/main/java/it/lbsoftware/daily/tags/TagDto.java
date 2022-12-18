package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.bases.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TagDto extends BaseDto {

  @NotBlank private String name;

  @NotNull
  @Pattern(regexp = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$")
  private String colorHex;
}
