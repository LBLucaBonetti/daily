import MoneyDto from 'src/interfaces/MoneyDto';

export function validateMoney(
  money: MoneyDto,
  translatedInvalidMessage: string,
) {
  return !money || isBlank(money.operationType) || money.amount <= 0
    ? translatedInvalidMessage
    : true;
}

function isBlank(str: string) {
  return (
    !str || str === '' || str.replaceAll(' ', '').replaceAll('\n', '') === ''
  );
}
