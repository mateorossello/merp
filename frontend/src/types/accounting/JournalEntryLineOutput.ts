export interface JournalEntryLineOutput {
  id: number;
  accountId: number;
  accountName: string;
  amount: number;
  debit: boolean;
  reference: string | null;
}
