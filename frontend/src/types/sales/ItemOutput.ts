export interface ItemOutput {
  id: number;
  code: string;
  name: string;
  description: string;
  available: boolean;
  unitPrice: number;
  currentStock: number;
  minimumStock: number;
  iva: number;
}
