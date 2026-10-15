package com.example.data.model

import java.util.Locale

enum class DayOfWeekEnum(val shortName: String, val fullName: String, val isoDayNumber: Int) {
    MONDAY("Mon", "Monday", 1),
    TUESDAY("Tue", "Tuesday", 2),
    WEDNESDAY("Wed", "Wednesday", 3),
    THURSDAY("Thu", "Thursday", 4),
    FRIDAY("Fri", "Friday", 5),
    SATURDAY("Sat", "Saturday", 6),
    SUNDAY("Sun", "Sunday", 7);

    companion object {
        fun fromIso(day: Int): DayOfWeekEnum {
            return entries.firstOrNull { it.isoDayNumber == day } ?: MONDAY
        }
    }
}

data class Course(
    val id: String,
    val name: String,
    val code: String,
    val lecturer: String,
    val lecturerEmail: String = "",
    val lecturerOffice: String = "",
    val classroom: String,
    val daysOfWeek: List<DayOfWeekEnum>,
    val startHour: Int, // 0 - 23
    val startMinute: Int, // 0 - 59
    val endHour: Int,
    val endMinute: Int,
    val colorIndex: Int = 0,
    val recurrence: String = "Weekly", // "Weekly", "Bi-weekly", "One-time"
    val notes: String = "",
    val reminderEnabled: Boolean = true
) {
    val durationMinutes: Int
        get() = (endHour * 60 + endMinute) - (startHour * 60 + startMinute)

    val formattedTimeRange: String
        get() {
            val startFormatted = formatTime(startHour, startMinute)
            val endFormatted = formatTime(endHour, endMinute)
            return "$startFormatted - $endFormatted"
        }

    val formattedDuration: String
        get() {
            val totalMin = durationMinutes
            val hrs = totalMin / 60
            val mins = totalMin % 60
            return when {
                hrs > 0 && mins > 0 -> "${hrs}h ${mins}m"
                hrs > 0 -> "${hrs}h"
                else -> "${mins}m"
            }
        }

    companion object {
        fun formatTime(hour: Int, minute: Int): String {
            val amPm = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm)
        }

        fun format24Hour(hour: Int, minute: Int): String {
            return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        }
    }
}

data class AppSettings(
    val reminderLeadMinutes: Int = 10, // 5, 10, 15, 30
    val themeMode: String = "system", // "system", "light", "dark"
    val weekStartDay: DayOfWeekEnum = DayOfWeekEnum.MONDAY,
    val soundEnabled: Boolean = true,
    val vibrateEnabled: Boolean = true
)
