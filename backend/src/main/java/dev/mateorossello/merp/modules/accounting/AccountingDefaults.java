package dev.mateorossello.merp.modules.accounting;

public final class AccountingDefaults {
    private AccountingDefaults() {

    }

    // ACTIVOS
    public static final String DEUDORES_POR_VENTAS = "1.1.2.1";
    public static final String MERCADERIAS         = "1.1.5.1";
    public static final String IVA_CREDITO_FISCAL  = "1.1.6.1";

    // PASIVOS
    public static final String PROVEEDORES         = "2.1.1.1";
    public static final String IVA_DEBITO_FISCAL   = "2.1.2.1";

    // RESULTADOS POSITIVOS
    public static final String VENTAS              = "4.1.1.1";

    // RESULTADOS NEGATIVOS
    public static final String CMV                 = "5.1.1.1";
    public static final String DEVOLUCIONES_VENTAS = "5.1.2.1";
    public static final String AJUSTES_VENTAS      = "5.1.3.1";
}
