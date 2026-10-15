package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.Course
import com.example.data.model.DayOfWeekEnum
import com.example.ui.theme.CoursePastelThemes
import com.example.ui.theme.VioletPrimary
import com.example.ui.viewmodel.TimetableViewModel
import java.util.Locale

private const val START_HOUR = 8
private const val END_HOUR = 21 // up to 9 PM
private val HOUR_HEIGHT = 64.dp
private val DAY_COLUMN_WIDTH = 112.dp
private val TIME_LABEL_WIDTH = 52.dp

@Composable
fun WeeklyGridScreen(
    courses: List<Course>,
    settings: AppSettings,
    onDayHeaderClicked: (DayOfWeekEnum) -> Unit,
    onCourseClicked: (String) -> Unit,
    onAddCourseClicked: () -> Unit
) {
    val today = remember { TimetableViewModel.getCurrentDayOfWeek() }

    // Order of days based on settings.weekStartDay
    val orderedDays = remember(settings.weekStartDay) {
        if (settings.weekStartDay == DayOfWeekEnum.SUNDAY) {
            listOf(
                DayOfWeekEnum.SUNDAY,
                DayOfWeekEnum.MONDAY,
                DayOfWeekEnum.TUESDAY,
                DayOfWeekEnum.WEDNESDAY,
                DayOfWeekEnum.THURSDAY,
                DayOfWeekEnum.FRIDAY,
                DayOfWeekEnum.SATURDAY
            )
        } else {
            listOf(
                DayOfWeekEnum.MONDAY,
                DayOfWeekEnum.TUESDAY,
                DayOfWeekEnum.WEDNESDAY,
                DayOfWeekEnum.THURSDAY,
                DayOfWeekEnum.FRIDAY,
                DayOfWeekEnum.SATURDAY,
                DayOfWeekEnum.SUNDAY
            )
        }
    }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            WeeklyHeaderBar(
                today = today,
                courseCount = courses.size,
                onTodayClicked = { onDayHeaderClicked(today) },
                onAddClicked = onAddCourseClicked
            )

            // Horizontally scrollable day headers + grid
            Column(modifier = Modifier.fillMaxSize()) {
                // Fixed Time Label corner + Horizontally scrollable headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                ) {
                    // Time column header spacer
                    Box(
                        modifier = Modifier
                            .width(TIME_LABEL_WIDTH)
                            .height(56.dp)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Weekly Grid",
                            tint = VioletPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Horizontally scrollable day headers
                    Row(
                        modifier = Modifier
                            .horizontalScroll(horizontalScrollState)
                            .padding(end = 16.dp)
                    ) {
                        orderedDays.forEach { day ->
                            val isToday = day == today
                            val dayCoursesCount = courses.count { it.daysOfWeek.contains(day) }
                            DayHeaderItem(
                                day = day,
                                isToday = isToday,
                                courseCount = dayCoursesCount,
                                onClick = { onDayHeaderClicked(day) }
                            )
                        }
                    }
                }

                // Grid Body: Synchronized Horizontal & Vertical scroll
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(verticalScrollState)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        // Time Column
                        TimeLabelsColumn()

                        // Day Columns
                        orderedDays.forEach { day ->
                            DayGridColumn(
                                day = day,
                                courses = courses.filter { it.daysOfWeek.contains(day) },
                                onCourseClicked = onCourseClicked
                            )
                        }
                    }
                }
            }
        }

        // Quick Add Floating Action Button
        FloatingActionButton(
            onClick = onAddCourseClicked,
            containerColor = VioletPrimary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("fab_add_course")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Course")
        }
    }
}

@Composable
private fun WeeklyHeaderBar(
    today: DayOfWeekEnum,
    courseCount: Int,
    onTodayClicked: () -> Unit,
    onAddClicked: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ClassMate",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = VioletPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = VioletPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Semester 1",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = VioletPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "$courseCount courses enrolled • Weekly Grid",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onTodayClicked() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Today,
                            contentDescription = "Today",
                            tint = VioletPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Today (${today.shortName})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayHeaderItem(
    day: DayOfWeekEnum,
    isToday: Boolean,
    courseCount: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(DAY_COLUMN_WIDTH)
            .height(56.dp)
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .testTag("day_header_${day.name}"),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isToday) VioletPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = day.shortName.uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    if (isToday) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
                Text(
                    text = if (courseCount == 1) "1 class" else "$courseCount classes",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = if (isToday) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimeLabelsColumn() {
    Column(
        modifier = Modifier
            .width(TIME_LABEL_WIDTH)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        for (h in START_HOUR..END_HOUR) {
            Box(
                modifier = Modifier
                    .width(TIME_LABEL_WIDTH)
                    .height(HOUR_HEIGHT)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                    .padding(top = 4.dp, end = 6.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                val displayHour = when {
                    h == 0 -> "12 AM"
                    h < 12 -> "$h AM"
                    h == 12 -> "12 PM"
                    else -> "${h - 12} PM"
                }
                Text(
                    text = displayHour,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DayGridColumn(
    day: DayOfWeekEnum,
    courses: List<Course>,
    onCourseClicked: (String) -> Unit
) {
    val totalHours = END_HOUR - START_HOUR + 1
    val totalHeight = HOUR_HEIGHT * totalHours

    Box(
        modifier = Modifier
            .width(DAY_COLUMN_WIDTH)
            .height(totalHeight)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )
    ) {
        // Horizontal hour grid guidelines
        Column(modifier = Modifier.fillMaxSize()) {
            repeat(totalHours) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HOUR_HEIGHT)
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                )
            }
        }

        // Placed Course Blocks
        courses.forEach { course ->
            val startMinutesFromBase = (course.startHour - START_HOUR) * 60 + course.startMinute
            val durationMin = course.durationMinutes.coerceAtLeast(30)

            val topOffsetDp = (startMinutesFromBase * HOUR_HEIGHT.value / 60f).dp
            val blockHeightDp = (durationMin * HOUR_HEIGHT.value / 60f).dp

            val pastel = CoursePastelThemes.getOrElse(course.colorIndex) { CoursePastelThemes[0] }

            Surface(
                shape = RoundedCornerShape(10.dp), // User requirement: rounded 10px event blocks
                color = pastel.lightBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, pastel.accentColor.copy(alpha = 0.8f)),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .offset(y = topOffsetDp)
                    .padding(horizontal = 3.dp, vertical = 1.dp)
                    .width(DAY_COLUMN_WIDTH - 6.dp)
                    .height(blockHeightDp - 2.dp)
                    .clickable { onCourseClicked(course.id) }
                    .testTag("weekly_course_block_${course.code}")
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left solid accent bar (calendar style)
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(pastel.accentColor)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = course.code,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = pastel.textColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = course.classroom,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = pastel.textColor.copy(alpha = 0.9f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = "${Course.format24Hour(course.startHour, course.startMinute)}-${Course.format24Hour(course.endHour, course.endMinute)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = pastel.textColor.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
