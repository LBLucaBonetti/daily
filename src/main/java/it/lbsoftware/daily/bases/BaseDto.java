package it.lbsoftware.daily.bases;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import it.lbsoftware.daily.config.Constants;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/** Base dto to extend for meaningful dtos that require common {@link BaseEntity} fields. */
@Getter
@Setter
@EqualsAndHashCode
public abstract class BaseDto {

  private UUID uuid;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(pattern = Constants.LOCALDATETIME_FORMAT)
  private LocalDateTime createdAt;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(pattern = Constants.LOCALDATETIME_FORMAT)
  private LocalDateTime updatedAt;
}
