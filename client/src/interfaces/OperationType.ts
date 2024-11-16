export type OperationType = 'INCOME' | 'OUTCOME';

export function isIncome(operationType: OperationType): boolean {
  return operationType === 'INCOME';
}

export function isOutcome(operationType: OperationType): boolean {
  return operationType === 'OUTCOME';
}
