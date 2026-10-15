package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.DayOfWeekEnum
import com.example.ui.navigation.BottomNavDestination
import com.example.ui.navigation.Screen
import com.example.ui.screens.AddEditCourseScreen
import com.example.ui.screens.CourseDetailScreen
import com.example.ui.screens.DailyViewScreen
import com.example.ui.screens.LecturerListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WeeklyGridScreen
import com.example.ui.theme.ClassMateTheme
import com.example.ui.theme.VioletPrimary
import com.example.ui.viewmodel.TimetableViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClassMateApp()
        }
    }
}

@Composable
fun ClassMateApp(viewModel: TimetableViewModel = viewModel()) {
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val selectedDay by viewModel.selectedDay.collectAsStateWithLifecycle()
    val currentTimeMinutes by viewModel.currentTimeMinutes.collectAsStateWithLifecycle()
    val lecturers by viewModel.lecturers.collectAsStateWithLifecycle()
    val lecturerSearchQuery by viewModel.lecturerSearchQuery.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Listen to user feedback messages
    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Determine dark mode from settings
    val systemDark = isSystemInDarkTheme()
    val isDark = when (settings.themeMode) {
        "dark" -> true
        "light" -> false
        else -> systemDark
    }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.WeeklyGrid) }
    var currentBottomTab by remember { mutableStateOf(BottomNavDestination.WEEKLY) }
    var previousScreen by remember { mutableStateOf<Screen>(Screen.WeeklyGrid) }

    ClassMateTheme(darkTheme = isDark) {
        // Back navigation handling
        BackHandler(enabled = currentScreen !is Screen.WeeklyGrid) {
            when (currentScreen) {
                is Screen.AddEditCourse, is Screen.CourseDetail -> {
                    currentScreen = previousScreen
                }
                else -> {
                    currentScreen = Screen.WeeklyGrid
                    currentBottomTab = BottomNavDestination.WEEKLY
                }
            }
        }

        val showBottomNav = currentScreen !is Screen.AddEditCourse && currentScreen !is Screen.CourseDetail

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomNav) {
                    NavigationBar(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .testTag("bottom_nav_bar"),
                        tonalElevation = 6.dp
                    ) {
                        // 1. Weekly Grid
                        NavigationBarItem(
                            selected = currentBottomTab == BottomNavDestination.WEEKLY && currentScreen is Screen.WeeklyGrid,
                            onClick = {
                                currentBottomTab = BottomNavDestination.WEEKLY
                                currentScreen = Screen.WeeklyGrid
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentBottomTab == BottomNavDestination.WEEKLY) Icons.Default.CalendarViewWeek else Icons.Outlined.CalendarViewWeek,
                                    contentDescription = "Weekly Grid"
                                )
                            },
                            label = { Text("Weekly") },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = VioletPrimary.copy(alpha = 0.2f)),
                            modifier = Modifier.testTag("nav_item_weekly")
                        )

                        // 2. Daily View
                        NavigationBarItem(
                            selected = currentBottomTab == BottomNavDestination.DAILY && currentScreen is Screen.DailyView,
                            onClick = {
                                currentBottomTab = BottomNavDestination.DAILY
                                currentScreen = Screen.DailyView(selectedDay)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentBottomTab == BottomNavDestination.DAILY) Icons.Default.Schedule else Icons.Outlined.Schedule,
                                    contentDescription = "Daily View"
                                )
                            },
                            label = { Text("Daily") },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = VioletPrimary.copy(alpha = 0.2f)),
                            modifier = Modifier.testTag("nav_item_daily")
                        )

                        // 3. Lecturer List
                        NavigationBarItem(
                            selected = currentBottomTab == BottomNavDestination.LECTURERS && currentScreen is Screen.LecturerList,
                            onClick = {
                                currentBottomTab = BottomNavDestination.LECTURERS
                                currentScreen = Screen.LecturerList
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentBottomTab == BottomNavDestination.LECTURERS) Icons.Default.School else Icons.Outlined.School,
                                    contentDescription = "Lecturers"
                                )
                            },
                            label = { Text("Lecturers") },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = VioletPrimary.copy(alpha = 0.2f)),
                            modifier = Modifier.testTag("nav_item_lecturers")
                        )

                        // 4. Settings
                        NavigationBarItem(
                            selected = currentBottomTab == BottomNavDestination.SETTINGS && currentScreen is Screen.Settings,
                            onClick = {
                                currentBottomTab = BottomNavDestination.SETTINGS
                                currentScreen = Screen.Settings
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentBottomTab == BottomNavDestination.SETTINGS) Icons.Default.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text("Settings") },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = VioletPrimary.copy(alpha = 0.2f)),
                            modifier = Modifier.testTag("nav_item_settings")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val screen = currentScreen) {
                    is Screen.WeeklyGrid -> {
                        WeeklyGridScreen(
                            courses = courses,
                            settings = settings,
                            onDayHeaderClicked = { day ->
                                viewModel.selectDay(day)
                                previousScreen = Screen.WeeklyGrid
                                currentBottomTab = BottomNavDestination.DAILY
                                currentScreen = Screen.DailyView(day)
                            },
                            onCourseClicked = { courseId ->
                                previousScreen = Screen.WeeklyGrid
                                currentScreen = Screen.CourseDetail(courseId)
                            },
                            onAddCourseClicked = {
                                previousScreen = Screen.WeeklyGrid
                                currentScreen = Screen.AddEditCourse(null)
                            }
                        )
                    }

                    is Screen.DailyView -> {
                        DailyViewScreen(
                            courses = courses,
                            selectedDay = selectedDay,
                            currentTimeMinutes = currentTimeMinutes,
                            settings = settings,
                            onDaySelected = { day ->
                                viewModel.selectDay(day)
                            },
                            onCourseClicked = { courseId ->
                                previousScreen = Screen.DailyView(selectedDay)
                                currentScreen = Screen.CourseDetail(courseId)
                            },
                            onToggleNotification = { course ->
                                viewModel.toggleNotification(course)
                            },
                            onAddCourseClicked = {
                                previousScreen = Screen.DailyView(selectedDay)
                                currentScreen = Screen.AddEditCourse(null)
                            }
                        )
                    }

                    is Screen.AddEditCourse -> {
                        val courseToEdit = courses.firstOrNull { it.id == screen.courseId }
                        AddEditCourseScreen(
                            courseToEdit = courseToEdit,
                            onSaveCourse = { course ->
                                viewModel.saveCourse(course)
                                currentScreen = previousScreen
                            },
                            onNavigateBack = {
                                currentScreen = previousScreen
                            }
                        )
                    }

                    is Screen.CourseDetail -> {
                        val course = courses.firstOrNull { it.id == screen.courseId }
                        CourseDetailScreen(
                            course = course,
                            settings = settings,
                            onNavigateBack = {
                                currentScreen = previousScreen
                            },
                            onEditClicked = { id ->
                                previousScreen = Screen.CourseDetail(id)
                                currentScreen = Screen.AddEditCourse(id)
                            },
                            onDeleteClicked = { id, code ->
                                viewModel.deleteCourse(id, code)
                                currentScreen = previousScreen
                            },
                            onToggleReminder = { c ->
                                viewModel.toggleNotification(c)
                            }
                        )
                    }

                    is Screen.LecturerList -> {
                        LecturerListScreen(
                            lecturers = lecturers,
                            searchQuery = lecturerSearchQuery,
                            onSearchQueryChanged = { viewModel.setLecturerSearchQuery(it) },
                            onCourseClicked = { courseId ->
                                previousScreen = Screen.LecturerList
                                currentScreen = Screen.CourseDetail(courseId)
                            }
                        )
                    }

                    is Screen.Settings -> {
                        SettingsScreen(
                            settings = settings,
                            onSettingsChanged = { updated ->
                                viewModel.updateSettings(updated)
                            },
                            onResetSampleData = {
                                viewModel.resetToSampleData()
                            },
                            onClearAllData = {
                                viewModel.clearAllData()
                            },
                            onTestNotification = {
                                viewModel.sendTestNotification()
                            }
                        )
                    }
                }
            }
        }
    }
}
