import type { AccountType } from "./AccountOutput";

export interface AccountInput {
  parentAccountId: number | null;
  code: string;
  type: AccountType | null;
  name: string;
  description: string;
}
