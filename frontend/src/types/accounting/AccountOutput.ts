export const ACCOUNT_TYPES = [
  "ASSET",
  "LIABILITY",
  "EQUITY",
  "REVENUE",
  "EXPENSE",
] as const;

export type AccountType = (typeof ACCOUNT_TYPES)[number];

export interface AccountOutput {
  id: number;
  parentAccountId: number | null;
  code: string;
  type: AccountType;
  name: string;
  description: string;
  receiveBalance: boolean;
  state: boolean;
}
