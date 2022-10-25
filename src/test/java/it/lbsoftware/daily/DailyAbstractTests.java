package it.lbsoftware.daily;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "okta"})
@TestMethodOrder(MethodOrderer.Random.class)
public sealed class DailyAbstractTests
    permits DailyAbstractIntegrationTests, DailyAbstractUnitTests {}
