package it.lbsoftware.daily.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DailyConfig {

  private final String baseUri;

  public DailyConfig(@Value("${daily.base-uri}") final String baseUri) {
    this.baseUri = baseUri;
  }
}
