package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppSettings
import com.example.data.model.Course
import com.example.data.model.DayOfWeekEnum
import com.example.data.repository.TimetableRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class LecturerItem(
    val name: String,
    val email: String,
    val office: String,
    val courses: List<Course>
)

class TimetableViewModel(application: Application) : AndroidViewModel(application) {
    val repository = TimetableRepository(application)

    val courses: StateFlow<List<Course>> = repository.allCourses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val settings: StateFlow<AppSettings> = repository.settings

    private val _selectedDay = MutableStateFlow(getCurrentDayOfWeek())
    val selectedDay: StateFlow<DayOfWeekEnum> = _selectedDay.asStateFlow()

    private val _currentTimeMinutes = MutableStateFlow(getCurrentTimeInMinutes())
    val currentTimeMinutes: StateFlow<Int> = _currentTimeMinutes.asStateFlow()

    private val _lecturerSearchQuery = MutableStateFlow("")
    val lecturerSearchQuery: StateFlow<String> = _lecturerSearchQuery.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    init {
        // Ticking loop to keep current-time line accurate
        viewModelScope.launch {
            while (true) {
                _currentTimeMinutes.value = getCurrentTimeInMinutes()
                delay(30_000) // update every 30 seconds
            }
        }
    }

    fun selectDay(day: DayOfWeekEnum) {
        _selectedDay.value = day
    }

    fun setLecturerSearchQuery(query: String) {
        _lecturerSearchQuery.value = query
    }

    fun saveCourse(course: Course) {
        viewModelScope.launch {
            repository.saveCourse(course)
            _userMessage.emit("Course '${course.code}' saved")
        }
    }

    fun deleteCourse(courseId: String, courseCode: String) {
        viewModelScope.launch {
            repository.deleteCourse(courseId)
            _userMessage.emit("Course '$courseCode' deleted")
        }
    }

    fun toggleNotification(course: Course) {
        viewModelScope.launch {
            val updated = course.copy(reminderEnabled = !course.reminderEnabled)
            repository.saveCourse(updated)
            val lead = settings.value.reminderLeadMinutes
            val msg = if (updated.reminderEnabled) {
                "Reminder set for $lead min before ${course.code}"
            } else {
                "Reminder turned off for ${course.code}"
            }
            _userMessage.emit(msg)
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        repository.updateSettings(newSettings)
        viewModelScope.launch {
            _userMessage.emit("Settings updated")
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.resetToSampleData()
            _userMessage.emit("Sample timetable loaded")
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAll()
            _userMessage.emit("All courses cleared")
        }
    }

    fun sendTestNotification() {
        viewModelScope.launch {
            val lead = settings.value.reminderLeadMinutes
            _userMessage.emit("🔔 Alert: Next class starts in $lead minutes! (${settings.value.weekStartDay.shortName})")
        }
    }

    val lecturers: StateFlow<List<LecturerItem>> = combine(courses, lecturerSearchQuery) { courseList, query ->
        val map = linkedMapOf<String, MutableList<Course>>()
        val emailMap = mutableMapOf<String, String>()
        val officeMap = mutableMapOf<String, String>()

        for (c in courseList) {
            val name = c.lecturer.trim()
            if (name.isNotEmpty()) {
                map.getOrOrPut(name) { mutableListOf() }.add(c)
                if (c.lecturerEmail.isNotBlank()) emailMap[name] = c.lecturerEmail
                if (c.lecturerOffice.isNotBlank()) officeMap[name] = c.lecturerOffice
            }
        }

        map.map { (name, courses) ->
            LecturerItem(
                name = name,
                email = emailMap[name] ?: "",
                office = officeMap[name] ?: "",
                courses = courses.distinctBy { it.code }
            )
        }.filter {
            if (query.isBlank()) true
            else {
                it.name.contains(query, ignoreCase = true) ||
                        it.courses.any { c -> c.code.contains(query, ignoreCase = true) || c.name.contains(query, ignoreCase = true) }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    companion object {
        fun getCurrentDayOfWeek(): DayOfWeekEnum {
            val cal = Calendar.getInstance()
            return when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> DayOfWeekEnum.MONDAY
                Calendar.TUESDAY -> DayOfWeekEnum.TUESDAY
                Calendar.WEDNESDAY -> DayOfWeekEnum.WEDNESDAY
                Calendar.THURSDAY -> DayOfWeekEnum.THURSDAY
                Calendar.FRIDAY -> DayOfWeekEnum.FRIDAY
                Calendar.SATURDAY -> DayOfWeekEnum.SATURDAY
                Calendar.SUNDAY -> DayOfWeekEnum.SUNDAY
                else -> DayOfWeekEnum.MONDAY
            }
        }

        fun getCurrentTimeInMinutes(): Int {
            val cal = Calendar.getInstance()
            return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        }
    }
}

private fun <K, V> MutableMap<K, V>.getOrOrPut(key: K, defaultValue: () -> V): V {
    val value = get(key)
    return if (value == null) {
        val answer = defaultValue()
        put(key, answer)
        answer
    } else {
        value
    }
}
