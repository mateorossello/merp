import type { JournalEntryLineOutput } from "./JournalEntryLineOutput";

export interface JournalEntryOutput {
  id: number;
  entryDate: string; // ISO 8601 ("AAAA-MM-DD")
  createdAt: string; // ISO 8601 ("AAAA-MM-DDTHH:mm:ss")
  description: string;
  createdByUserId: number;
  journalEntryLinesOutput: JournalEntryLineOutput[];
}
