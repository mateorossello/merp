export const ACCOUNT_TYPES = [
  "ACTIVO",
  "PASIVO",
  "PATRIMONIO_NETO",
  "RESULTADOS_POSITIVOS",
  "RESULTADOS_NEGATIVOS",
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
