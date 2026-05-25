package dev.mateorossello.merp.modules.access.models;

/**
 * Enum representing the different types of tasks (permissions) that can be assigned to profiles in the access system.
 */

public enum TaskType {
    MANAGE_USERS,
    VIEW_USERS,

    MANAGE_PROFILES,
    VIEW_PROFILES,

    VIEW_TASKS,

    MANAGE_ACCOUNTS,
    VIEW_ACCOUNTS,

    MANAGE_JOURNAL_ENTRIES,
    VIEW_JOURNAL_ENTRIES,
    
    VIEW_REPORTS,

    MANAGE_CUSTOMERS,
    VIEW_CUSTOMERS,

    MANAGE_FISCAL_CONFIGURATION,
    VIEW_FISCAL_CONFIGURATION,

    MANAGE_ITEMS,
    VIEW_ITEMS,

    MANAGE_PAYMENT_METHODS,
    VIEW_PAYMENT_METHODS,

    MANAGE_TRANSACTIONS,
    VIEW_TRANSACTIONS,

    MANAGE_DELIVERY_NOTES,
    VIEW_DELIVERY_NOTES,

    MANAGE_INVOICES,
    VIEW_INVOICES,

    MANAGE_NOTES,
    VIEW_NOTES
}
