package it.lbsoftware.daily.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

@Profile("!test")
@Configuration
@EnableAsync
public class AsyncConfiguration {
  // This class serves the only purpose of enabling the @Async methods whenever the test profile is
  // not selected. For tests to be simple, we avoid async processing
}
