import type { OperationType } from './OperationType';

export default interface MoneyDto {
  uuid: string;
  operationDate: Date;
  amount: number;
  operationType: OperationType;
  description?: string;
  createdAt?: Date;
  updatedAt?: Date;
}
