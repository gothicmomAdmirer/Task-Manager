# Task Manager with Sections and Deadlines

A Java-based desktop application for organizing tasks by sections and deadlines. This project utilizes **Swing** for GUI, **JCalendar** for date selection, and supports task persistence through file I/O.

## Features

- **Task Management**: Add, check off, and view tasks.
- **Section-Based Organization**: Group tasks into customizable sections (e.g., "School", "Work").
- **Deadline Integration**: Select task deadlines using a calendar widget.
- **Progress Tracking**: Tasks are automatically moved to a "Done Tasks" window when completed.
- **Data Persistence**: Sections and tasks are saved across sessions using text files.
- **Responsive UI**: Dynamically updates sections and task counts in real-time.



### Prerequisites
- Java JDK 8 or higher
- [JCalendar (toedter calendar)](https://toedter.com/jcalendar/) libraryFile Structure
	•	sections.txt – Stores all added sections.
	•	tasks.txt – Stores active tasks with their status.
	•	done_tasks.txt – Stores completed tasks.

       Technologies Used
	•	Java Swing (GUI)
	•	JCalendar (com.toedter.calendar.JDateChooser)
	•	File I/O
	•	HashMap & ArrayList for data structures
        Future Enhancements
	•	Add task priority levels.
	•	Edit and delete existing tasks.
	•	Search and filter functionality.
	•	Save/load using JSON or a database.

