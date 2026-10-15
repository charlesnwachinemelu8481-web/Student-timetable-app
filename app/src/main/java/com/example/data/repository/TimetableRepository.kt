package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.ClassMateDatabase
import com.example.data.local.CourseEntity
import com.example.data.model.AppSettings
import com.example.data.model.Course
import com.example.data.model.DayOfWeekEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimetableRepository(private val context: Context) {
    private val database = ClassMateDatabase.getDatabase(context)
    private val courseDao = database.courseDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("classmate_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (courseDao.getCount() == 0) {
                resetToSampleData()
            }
        }
    }

    val allCourses: Flow<List<Course>> = courseDao.getAllCourses().map { entities ->
        entities.map { it.toCourse() }
    }

    suspend fun getCourseById(id: String): Course? = withContext(Dispatchers.IO) {
        courseDao.getCourseById(id)?.toCourse()
    }

    suspend fun saveCourse(course: Course) = withContext(Dispatchers.IO) {
        courseDao.insertCourse(CourseEntity.fromCourse(course))
    }

    suspend fun deleteCourse(id: String) = withContext(Dispatchers.IO) {
        courseDao.deleteCourseById(id)
    }

    suspend fun resetToSampleData() = withContext(Dispatchers.IO) {
        courseDao.clearAll()
        val entities = SampleData.sampleCourses.map { CourseEntity.fromCourse(it) }
        courseDao.insertCourses(entities)
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        courseDao.clearAll()
    }

    private fun loadSettings(): AppSettings {
        val leadMin = prefs.getInt("reminder_lead_min", 10)
        val theme = prefs.getString("theme_mode", "system") ?: "system"
        val weekStart = prefs.getString("week_start_day", DayOfWeekEnum.MONDAY.name) ?: DayOfWeekEnum.MONDAY.name
        val sound = prefs.getBoolean("sound_enabled", true)
        val vibrate = prefs.getBoolean("vibrate_enabled", true)

        val weekStartEnum = try {
            DayOfWeekEnum.valueOf(weekStart)
        } catch (e: Exception) {
            DayOfWeekEnum.MONDAY
        }

        return AppSettings(
            reminderLeadMinutes = leadMin,
            themeMode = theme,
            weekStartDay = weekStartEnum,
            soundEnabled = sound,
            vibrateEnabled = vibrate
        )
    }

    fun updateSettings(newSettings: AppSettings) {
        prefs.edit()
            .putInt("reminder_lead_min", newSettings.reminderLeadMinutes)
            .putString("theme_mode", newSettings.themeMode)
            .putString("week_start_day", newSettings.weekStartDay.name)
            .putBoolean("sound_enabled", newSettings.soundEnabled)
            .putBoolean("vibrate_enabled", newSettings.vibrateEnabled)
            .apply()
        _settings.value = newSettings
    }
}
