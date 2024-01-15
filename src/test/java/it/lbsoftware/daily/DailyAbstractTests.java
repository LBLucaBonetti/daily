package it.lbsoftware.daily;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "oauth2"})
@TestMethodOrder(MethodOrderer.Random.class)
@DisplayNameGeneration(TestUtils.DailyDisplayNameGenerator.class)
public sealed class DailyAbstractTests
    permits DailyAbstractIntegrationTests, DailyAbstractUnitTests {}
