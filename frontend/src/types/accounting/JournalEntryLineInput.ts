export interface JournalEntryLineInput {
  accountId: number | null;
  amount: number | null;
  debit: boolean | null;
  reference: string;
}
