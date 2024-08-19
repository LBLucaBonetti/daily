package it.lbsoftware.daily.money;

import it.lbsoftware.daily.DailyAbstractUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

class MoneyServiceTests extends DailyAbstractUnitTests {

  @Mock private MoneyRepository moneyRepository;
  private MoneyService moneyService;

  @BeforeEach
  void beforeEach() {
    moneyService = new MoneyService(moneyRepository);
  }
}
