package it.lbsoftware.daily.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This class serves the only purpose of enabling the @Scheduled methods whenever the configuration
 * is true. For tests to be simple, we avoid scheduling processing.
 */
@ConditionalOnProperty(
    name = Constants.DAILY_SCHEDULING_ENABLED,
    havingValue = "true",
    matchIfMissing = false)
@Configuration
@EnableScheduling
public class SchedulingConfiguration {}
