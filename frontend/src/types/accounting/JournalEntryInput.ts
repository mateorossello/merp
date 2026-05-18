import type { JournalEntryLineInput } from "./JournalEntryLineInput";

export interface JournalEntryInput {
  entryDate: string;
  description: string;
  journalEntryLinesInput: JournalEntryLineInput[];
}
