package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.bases.BaseDto;
import it.lbsoftware.daily.config.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TagDto extends BaseDto {

  @NotBlank
  @Size(max = Constants.TAG_NAME_MAX)
  private String name;

  @NotNull
  @Pattern(regexp = Constants.TAG_COLOR_HEX_REGEXP)
  private String colorHex;
}
