package dev.mateorossello.merp.modules.accounting.utils;

import dev.mateorossello.merp.exceptions.ResourceConflictException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DateRangeValidator {
    private DateRangeValidator() {

    }

    public static void validate(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ResourceConflictException("Start date cannot be after end date.");
        }
    }

    public static void validate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ResourceConflictException("Start date cannot be after end date.");
        }
    }
}
