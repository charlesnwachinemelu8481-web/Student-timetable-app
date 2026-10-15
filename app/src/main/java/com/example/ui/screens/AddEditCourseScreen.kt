package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.DayOfWeekEnum
import com.example.ui.components.ClassMateTimePickerDialog
import com.example.ui.components.PastelColorPicker
import com.example.ui.theme.VioletPrimary
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditCourseScreen(
    courseToEdit: Course?,
    onSaveCourse: (Course) -> Unit,
    onNavigateBack: () -> Unit
) {
    val isEditMode = courseToEdit != null

    var name by remember { mutableStateOf(courseToEdit?.name ?: "") }
    var code by remember { mutableStateOf(courseToEdit?.code ?: "") }
    var lecturer by remember { mutableStateOf(courseToEdit?.lecturer ?: "") }
    var lecturerEmail by remember { mutableStateOf(courseToEdit?.lecturerEmail ?: "") }
    var lecturerOffice by remember { mutableStateOf(courseToEdit?.lecturerOffice ?: "") }
    var classroom by remember { mutableStateOf(courseToEdit?.classroom ?: "") }

    val selectedDays = remember {
        mutableStateListOf<DayOfWeekEnum>().apply {
            if (courseToEdit != null) {
                addAll(courseToEdit.daysOfWeek)
            } else {
                add(DayOfWeekEnum.MONDAY)
                add(DayOfWeekEnum.WEDNESDAY)
            }
        }
    }

    var startHour by remember { mutableIntStateOf(courseToEdit?.startHour ?: 9) }
    var startMinute by remember { mutableIntStateOf(courseToEdit?.startMinute ?: 0) }
    var endHour by remember { mutableIntStateOf(courseToEdit?.endHour ?: 10) }
    var endMinute by remember { mutableIntStateOf(courseToEdit?.endMinute ?: 30) }

    var colorIndex by remember { mutableIntStateOf(courseToEdit?.colorIndex ?: 0) }
    var recurrence by remember { mutableStateOf(courseToEdit?.recurrence ?: "Weekly") }
    var reminderEnabled by remember { mutableStateOf(courseToEdit?.reminderEnabled ?: true) }
    var notes by remember { mutableStateOf(courseToEdit?.notes ?: "") }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showStartTimePicker) {
        ClassMateTimePickerDialog(
            title = "Class Start Time",
            initialHour = startHour,
            initialMinute = startMinute,
            onDismiss = { showStartTimePicker = false },
            onTimeSelected = { h, m ->
                startHour = h
                startMinute = m
                showStartTimePicker = false
                // Auto adjust end time if it ends before start time
                if (endHour * 60 + endMinute <= h * 60 + m) {
                    endHour = (h + 1).coerceAtMost(23)
                    endMinute = m
                }
            }
        )
    }

    if (showEndTimePicker) {
        ClassMateTimePickerDialog(
            title = "Class End Time",
            initialHour = endHour,
            initialMinute = endMinute,
            onDismiss = { showEndTimePicker = false },
            onTimeSelected = { h, m ->
                endHour = h
                endMinute = m
                showEndTimePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Course" else "Add New Course",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("add_edit_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Please enter the course name"
                                return@Button
                            }
                            if (code.isBlank()) {
                                errorMessage = "Please enter the course code (e.g. CS 101)"
                                return@Button
                            }
                            if (classroom.isBlank()) {
                                errorMessage = "Please enter the classroom/hall"
                                return@Button
                            }
                            if (selectedDays.isEmpty()) {
                                errorMessage = "Please select at least one day of the week"
                                return@Button
                            }
                            val startMin = startHour * 60 + startMinute
                            val endMin = endHour * 60 + endMinute
                            if (endMin <= startMin) {
                                errorMessage = "End time must be after start time"
                                return@Button
                            }

                            val course = Course(
                                id = courseToEdit?.id ?: UUID.randomUUID().toString(),
                                name = name.trim(),
                                code = code.trim().uppercase(),
                                lecturer = lecturer.trim().ifEmpty { "Instructor" },
                                lecturerEmail = lecturerEmail.trim(),
                                lecturerOffice = lecturerOffice.trim(),
                                classroom = classroom.trim(),
                                daysOfWeek = selectedDays.toList(),
                                startHour = startHour,
                                startMinute = startMinute,
                                endHour = endHour,
                                endMinute = endMinute,
                                colorIndex = colorIndex,
                                recurrence = recurrence,
                                notes = notes.trim(),
                                reminderEnabled = reminderEnabled
                            )
                            onSaveCourse(course)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("save_course_button")
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Message Banner if validation fails
            errorMessage?.let { err ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // 1. Basic Info Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Course Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = null },
                        label = { Text("Course Name *") },
                        placeholder = { Text("e.g. Data Structures & Algorithms") },
                        leadingIcon = { Icon(Icons.Default.Book, contentDescription = null, tint = VioletPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_course_name"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it; errorMessage = null },
                            label = { Text("Course Code *") },
                            placeholder = { Text("CS 201") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_course_code"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = classroom,
                            onValueChange = { classroom = it; errorMessage = null },
                            label = { Text("Classroom/Hall *") },
                            placeholder = { Text("Hall 304") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = VioletPrimary) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_classroom"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }
            }

            // 2. Schedule & Days Multi-Select
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Schedule & Timing",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Day(s) of Week (Multi-select)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DayOfWeekEnum.entries.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        selectedDays.remove(day)
                                    } else {
                                        selectedDays.add(day)
                                    }
                                    errorMessage = null
                                },
                                label = { Text(day.shortName) },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VioletPrimary,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                ),
                                modifier = Modifier.testTag("day_chip_${day.name}")
                            )
                        }
                    }

                    // Start and End Time Pickers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Start Time Selector Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showStartTimePicker = true }
                                .testTag("select_start_time_button")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Starts at",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = VioletPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = Course.formatTime(startHour, startMinute),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // End Time Selector Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showEndTimePicker = true }
                                .testTag("select_end_time_button")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Ends at",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = VioletPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = Course.formatTime(endHour, endMinute),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Recurrence Toggle
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Repeat, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recurrence",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Weekly", "Bi-weekly", "One-time").forEach { option ->
                                val isSelected = recurrence == option
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { recurrence = option },
                                    label = { Text(option) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = VioletPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("recurrence_$option")
                                )
                            }
                        }
                    }
                }
            }

            // 3. Pastel Color Picker Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PastelColorPicker(
                        selectedIndex = colorIndex,
                        onColorSelected = { colorIndex = it }
                    )
                }
            }

            // 4. Lecturer & Contact Info
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Lecturer Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = lecturer,
                        onValueChange = { lecturer = it },
                        label = { Text("Lecturer / Professor Name") },
                        placeholder = { Text("Dr. Alan Turing") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = VioletPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_lecturer_name"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = lecturerEmail,
                        onValueChange = { lecturerEmail = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("lecturer@university.edu") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = VioletPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_lecturer_email"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = lecturerOffice,
                        onValueChange = { lecturerOffice = it },
                        label = { Text("Office Room / Hours") },
                        placeholder = { Text("Science Wing 304, Tue 2-4 PM") },
                        leadingIcon = { Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = VioletPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_lecturer_office"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }

            // 5. Notifications & Notes
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Class Reminder Notification",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Get notified before every class session",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = VioletPrimary, checkedTrackColor = VioletPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.testTag("switch_reminder")
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Syllabus & Course Notes (Optional)") },
                        placeholder = { Text("Key textbook references, exam dates, group assignment...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_course_notes"),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
