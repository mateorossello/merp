import type { AccountMovementOutput } from "./AccountMovementOutput";

export interface GeneralLedgerOutput {
  accountId: number;
  accountName: string;
  startDate: string;
  endDate: string;
  initialBalance: number;
  accountMovementsOutput: AccountMovementOutput[];
}
