package com.example.ui.navigation

import com.example.data.model.DayOfWeekEnum

sealed interface Screen {
    object WeeklyGrid : Screen
    data class DailyView(val day: DayOfWeekEnum? = null) : Screen
    data class AddEditCourse(val courseId: String? = null) : Screen
    data class CourseDetail(val courseId: String) : Screen
    object LecturerList : Screen
    object Settings : Screen
}

enum class BottomNavDestination(val label: String) {
    WEEKLY("Weekly Grid"),
    DAILY("Daily View"),
    LECTURERS("Lecturers"),
    SETTINGS("Settings")
}
