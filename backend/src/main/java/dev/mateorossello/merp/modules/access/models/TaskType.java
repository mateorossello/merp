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
    
    VIEW_REPORTS
}
