package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.data.model.Course
import com.example.data.model.DayOfWeekEnum

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val code: String,
    val lecturer: String,
    val lecturerEmail: String,
    val lecturerOffice: String,
    val classroom: String,
    val daysOfWeek: String, // comma separated e.g. "MONDAY,WEDNESDAY,FRIDAY"
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val colorIndex: Int,
    val recurrence: String,
    val notes: String,
    val reminderEnabled: Boolean
) {
    fun toCourse(): Course {
        val days = if (daysOfWeek.isBlank()) {
            emptyList()
        } else {
            daysOfWeek.split(",").mapNotNull { name ->
                try {
                    DayOfWeekEnum.valueOf(name.trim())
                } catch (e: Exception) {
                    null
                }
            }
        }
        return Course(
            id = id,
            name = name,
            code = code,
            lecturer = lecturer,
            lecturerEmail = lecturerEmail,
            lecturerOffice = lecturerOffice,
            classroom = classroom,
            daysOfWeek = days,
            startHour = startHour,
            startMinute = startMinute,
            endHour = endHour,
            endMinute = endMinute,
            colorIndex = colorIndex,
            recurrence = recurrence,
            notes = notes,
            reminderEnabled = reminderEnabled
        )
    }

    companion object {
        fun fromCourse(course: Course): CourseEntity {
            return CourseEntity(
                id = course.id,
                name = course.name,
                code = course.code,
                lecturer = course.lecturer,
                lecturerEmail = course.lecturerEmail,
                lecturerOffice = course.lecturerOffice,
                classroom = course.classroom,
                daysOfWeek = course.daysOfWeek.joinToString(",") { it.name },
                startHour = course.startHour,
                startMinute = course.startMinute,
                endHour = course.endHour,
                endMinute = course.endMinute,
                colorIndex = course.colorIndex,
                recurrence = course.recurrence,
                notes = course.notes,
                reminderEnabled = course.reminderEnabled
            )
        }
    }
}
