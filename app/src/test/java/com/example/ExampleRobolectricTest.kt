package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Course
import com.example.data.model.DayOfWeekEnum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ClassMate", appName)
  }

  @Test
  fun `course duration calculation works correctly`() {
    val course = Course(
      id = "test_1",
      name = "Operating Systems",
      code = "CS 301",
      lecturer = "Prof. Tanenbaum",
      classroom = "Lab 4",
      daysOfWeek = listOf(DayOfWeekEnum.MONDAY, DayOfWeekEnum.WEDNESDAY),
      startHour = 9,
      startMinute = 0,
      endHour = 10,
      endMinute = 30,
      colorIndex = 1
    )
    assertEquals(90, course.durationMinutes)
    assertEquals("1h 30m", course.formattedDuration)
    assertTrue(course.formattedTimeRange.contains("09:00"))
    assertTrue(course.formattedTimeRange.contains("10:30"))
  }
}
