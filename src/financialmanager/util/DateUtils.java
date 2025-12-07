package financialmanager.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {

    private DateUtils() {
        // Приватный конструктор
    }

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String formatForDisplay(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }

    public static LocalDate parseFromDisplay(String dateString) {
        return LocalDate.parse(dateString, DISPLAY_FORMATTER);
    }

    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static boolean isDateInRange(LocalDate date, LocalDate start, LocalDate end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
}