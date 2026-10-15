package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.Course
import com.example.data.model.DayOfWeekEnum
import com.example.ui.components.CampusDirectionsDialog
import com.example.ui.theme.CoursePastelThemes
import com.example.ui.theme.CurrentTimeIndicator
import com.example.ui.theme.VioletPrimary
import com.example.ui.viewmodel.TimetableViewModel

@Composable
fun DailyViewScreen(
    courses: List<Course>,
    selectedDay: DayOfWeekEnum,
    currentTimeMinutes: Int,
    settings: AppSettings,
    onDaySelected: (DayOfWeekEnum) -> Unit,
    onCourseClicked: (String) -> Unit,
    onToggleNotification: (Course) -> Unit,
    onAddCourseClicked: () -> Unit
) {
    val today = remember { TimetableViewModel.getCurrentDayOfWeek() }
    val isTodaySelected = selectedDay == today

    // Courses for the selected day, sorted by start time
    val dayCourses = remember(courses, selectedDay) {
        courses.filter { it.daysOfWeek.contains(selectedDay) }
            .sortedWith(compareBy({ it.startHour }, { it.startMinute }))
    }

    var directionsCourse by remember { mutableStateOf<Course?>(null) }

    // Dialog for directions
    directionsCourse?.let { course ->
        CampusDirectionsDialog(
            course = course,
            onDismiss = { directionsCourse = null }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Timeline",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${selectedDay.fullName} • ${dayCourses.size} classes scheduled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!isTodaySelected) {
                            OutlinedButton(
                                onClick = { onDaySelected(today) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("jump_to_today_button")
                            ) {
                                Text("Jump to Today", fontSize = 12.sp, color = VioletPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Day selector horizontal chip row
                    val daysOrder = if (settings.weekStartDay == DayOfWeekEnum.SUNDAY) {
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        daysOrder.forEach { day ->
                            val isSelected = day == selectedDay
                            val isCurrentDay = day == today
                            val count = courses.count { it.daysOfWeek.contains(day) }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) VioletPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { onDaySelected(day) }
                                    .testTag("daily_tab_${day.name}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = day.shortName,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isCurrentDay && !isSelected) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(VioletPrimary)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "$count",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Current Time Indicator Banner if today
            if (isTodaySelected) {
                val currentHour = currentTimeMinutes / 60
                val currentMin = currentTimeMinutes % 60
                Surface(
                    color = VioletPrimary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CurrentTimeIndicator)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Current Time: ${Course.formatTime(currentHour, currentMin)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = VioletPrimary
                            )
                        }

                        val ongoingOrNext = dayCourses.firstOrNull {
                            val startMin = it.startHour * 60 + it.startMinute
                            val endMin = it.endHour * 60 + it.endMinute
                            currentTimeMinutes in startMin..endMin
                        } ?: dayCourses.firstOrNull {
                            val startMin = it.startHour * 60 + it.startMinute
                            startMin > currentTimeMinutes
                        }

                        if (ongoingOrNext != null) {
                            Text(
                                text = "Up next: ${ongoingOrNext.code}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Timeline List of Course Cards
            if (dayCourses.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = VioletPrimary.copy(alpha = 0.1f),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.EventBusy,
                                    contentDescription = "No classes",
                                    tint = VioletPrimary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Classes on ${selectedDay.fullName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Take time to rest, study, or plan ahead.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddCourseClicked,
                            colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Schedule Class for ${selectedDay.shortName}")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 14.dp, bottom = 96.dp)
                ) {
                    items(dayCourses, key = { it.id }) { course ->
                        DailyCourseCard(
                            course = course,
                            isToday = isTodaySelected,
                            currentTimeMinutes = currentTimeMinutes,
                            onCardClicked = { onCourseClicked(course.id) },
                            onGetDirections = { directionsCourse = course },
                            onToggleNotify = { onToggleNotification(course) }
                        )
                    }
                }
            }
        }

        // Add Course FAB
        FloatingActionButton(
            onClick = onAddCourseClicked,
            containerColor = VioletPrimary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("daily_fab_add")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Course")
        }
    }
}

@Composable
private fun DailyCourseCard(
    course: Course,
    isToday: Boolean,
    currentTimeMinutes: Int,
    onCardClicked: () -> Unit,
    onGetDirections: () -> Unit,
    onToggleNotify: () -> Unit
) {
    val pastel = CoursePastelThemes.getOrElse(course.colorIndex) { CoursePastelThemes[0] }

    val courseStartMinutes = course.startHour * 60 + course.startMinute
    val courseEndMinutes = course.endHour * 60 + course.endMinute

    val isOngoing = isToday && (currentTimeMinutes in courseStartMinutes..courseEndMinutes)
    val isPast = isToday && (currentTimeMinutes > courseEndMinutes)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Timeline Header: Time range badge & Status indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isOngoing) CurrentTimeIndicator else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = course.formattedTimeRange,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isOngoing) CurrentTimeIndicator else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• ${course.formattedDuration}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isOngoing) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CurrentTimeIndicator.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "NOW IN SESSION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CurrentTimeIndicator,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else if (isPast) {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }

        // Live Current-Time indicator bar when ongoing
        if (isOngoing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(CurrentTimeIndicator)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(CurrentTimeIndicator)
                )
            }
        }

        // Main Course Card: rounded 10px / 12px with pastel background
        Card(
            shape = RoundedCornerShape(10.dp), // User requirement: rounded 10px event blocks
            colors = CardDefaults.cardColors(containerColor = pastel.lightBackground),
            border = androidx.compose.foundation.BorderStroke(
                width = if (isOngoing) 2.dp else 1.dp,
                color = if (isOngoing) CurrentTimeIndicator else pastel.accentColor.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCardClicked() }
                .testTag("course_card_${course.code}")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Top row: Course Code badge + Recurrence pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = pastel.accentColor
                    ) {
                        Text(
                            text = course.code,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = pastel.textColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = course.recurrence,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = pastel.textColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Course Name
                Text(
                    text = course.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = pastel.textColor
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Lecturer & Room Info Rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Lecturer
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = pastel.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = course.lecturer,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = pastel.textColor
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Room
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = pastel.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = course.classroom,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = pastel.textColor.copy(alpha = 0.85f)
                            )
                        }
                    }

                    // Action Icons: "Get Directions" and "Notify Me"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // "Get Directions" Action Button
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onGetDirections() }
                                .testTag("directions_button_${course.code}"),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.NearMe,
                                    contentDescription = "Get Directions to ${course.classroom}",
                                    tint = pastel.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // "Notify Me" Action Button
                        Surface(
                            shape = CircleShape,
                            color = if (course.reminderEnabled) pastel.accentColor else Color.White.copy(alpha = 0.85f),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onToggleNotify() }
                                .testTag("notify_button_${course.code}"),
                            shadowElevation = 1.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (course.reminderEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = "Notify me for ${course.code}",
                                    tint = if (course.reminderEnabled) Color.White else pastel.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
