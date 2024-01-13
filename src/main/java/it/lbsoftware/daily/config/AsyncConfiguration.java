package it.lbsoftware.daily.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * This class serves the only purpose of enabling the @Async methods whenever the configuration is
 * true. For tests to be simple, we avoid async processing
 */
@ConditionalOnProperty(
    name = Constants.DAILY_ASYNC_ENABLED,
    havingValue = "true",
    matchIfMissing = false)
@Configuration
@EnableAsync
public class AsyncConfiguration {}
