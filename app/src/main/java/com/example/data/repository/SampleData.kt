package com.example.data.repository

import com.example.data.model.Course
import com.example.data.model.DayOfWeekEnum
import java.util.UUID

object SampleData {
    val sampleCourses = listOf(
        Course(
            id = "sample_cs201",
            name = "Data Structures & Algorithms",
            code = "CS 201",
            lecturer = "Dr. Alan Turing",
            lecturerEmail = "a.turing@university.edu",
            lecturerOffice = "Turing Science Wing, Room 304",
            classroom = "Science Complex 102",
            daysOfWeek = listOf(DayOfWeekEnum.MONDAY, DayOfWeekEnum.WEDNESDAY, DayOfWeekEnum.FRIDAY),
            startHour = 9,
            startMinute = 0,
            endHour = 10,
            endMinute = 30,
            colorIndex = 0, // Lavender Violet
            recurrence = "Weekly",
            notes = "Midterm in week 7. Bring laptop with Kotlin / Java IDE installed.",
            reminderEnabled = true
        ),
        Course(
            id = "sample_math220",
            name = "Linear Algebra & Matrices",
            code = "MATH 220",
            lecturer = "Prof. Katherine Johnson",
            lecturerEmail = "k.johnson@university.edu",
            lecturerOffice = "Euler Mathematical Institute, Room 210",
            classroom = "Math Hall 204",
            daysOfWeek = listOf(DayOfWeekEnum.TUESDAY, DayOfWeekEnum.THURSDAY),
            startHour = 10,
            startMinute = 0,
            endHour = 11,
            endMinute = 30,
            colorIndex = 1, // Sky Blue
            recurrence = "Weekly",
            notes = "Problem sets due every Thursday at 5 PM on student portal.",
            reminderEnabled = true
        ),
        Course(
            id = "sample_phys150",
            name = "Physics: Mechanics & Waves",
            code = "PHYS 150",
            lecturer = "Dr. Richard Feynman",
            lecturerEmail = "r.feynman@university.edu",
            lecturerOffice = "Bohr Physics Tower, Room 405",
            classroom = "Physics Lab 3A",
            daysOfWeek = listOf(DayOfWeekEnum.MONDAY, DayOfWeekEnum.WEDNESDAY),
            startHour = 11,
            startMinute = 0,
            endHour = 12,
            endMinute = 30,
            colorIndex = 2, // Emerald Mint
            recurrence = "Weekly",
            notes = "Safety goggles required for Wednesday laboratory sessions.",
            reminderEnabled = true
        ),
        Course(
            id = "sample_eng102",
            name = "Academic Writing & Research",
            code = "ENG 102",
            lecturer = "Prof. Maya Angelou",
            lecturerEmail = "m.angelou@university.edu",
            lecturerOffice = "Humanities Annex, Room 118",
            classroom = "Library Seminar Rm 2",
            daysOfWeek = listOf(DayOfWeekEnum.TUESDAY, DayOfWeekEnum.THURSDAY),
            startHour = 13,
            startMinute = 0,
            endHour = 14,
            endMinute = 30,
            colorIndex = 3, // Rose Pink
            recurrence = "Weekly",
            notes = "Peer review sessions on alternate Thursdays.",
            reminderEnabled = true
        ),
        Course(
            id = "sample_bio110",
            name = "Cellular & Molecular Biology",
            code = "BIO 110",
            lecturer = "Dr. Rosalind Franklin",
            lecturerEmail = "r.franklin@university.edu",
            lecturerOffice = "Franklin BioCenter, Room 502",
            classroom = "BioMed Hall 101",
            daysOfWeek = listOf(DayOfWeekEnum.MONDAY, DayOfWeekEnum.WEDNESDAY),
            startHour = 14,
            startMinute = 0,
            endHour = 15,
            endMinute = 30,
            colorIndex = 5, // Warm Peach
            recurrence = "Weekly",
            notes = "Dissection lab scheduled for third week of next month.",
            reminderEnabled = false
        ),
        Course(
            id = "sample_cs340",
            name = "Database Systems & SQL",
            code = "CS 340",
            lecturer = "Dr. Alan Turing",
            lecturerEmail = "a.turing@university.edu",
            lecturerOffice = "Turing Science Wing, Room 304",
            classroom = "Science Complex 204",
            daysOfWeek = listOf(DayOfWeekEnum.TUESDAY, DayOfWeekEnum.THURSDAY),
            startHour = 15,
            startMinute = 0,
            endHour = 16,
            endMinute = 30,
            colorIndex = 6, // Indigo Dusk
            recurrence = "Weekly",
            notes = "Final project teams will be assigned next Tuesday.",
            reminderEnabled = true
        ),
        Course(
            id = "sample_art205",
            name = "Digital Media & UI Design",
            code = "ART 205",
            lecturer = "Prof. Dieter Rams",
            lecturerEmail = "d.rams@university.edu",
            lecturerOffice = "Design Studio B, Rm 12",
            classroom = "Creative Arts Studio 4",
            daysOfWeek = listOf(DayOfWeekEnum.FRIDAY),
            startHour = 13,
            startMinute = 30,
            endHour = 16,
            endMinute = 30,
            colorIndex = 4, // Amber Sun
            recurrence = "Weekly",
            notes = "Bring sketchbook and tablet with Figma / vector software.",
            reminderEnabled = true
        )
    )
}
