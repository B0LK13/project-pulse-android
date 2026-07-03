package com.projectpulse.android.data.local

import com.projectpulse.android.data.local.dao.ProjectDao
import com.projectpulse.android.data.local.dao.TaskDao
import com.projectpulse.android.data.local.dao.TimeLogDao
import com.projectpulse.android.data.local.entity.ProjectEntity
import com.projectpulse.android.data.local.entity.TaskEntity
import com.projectpulse.android.data.local.entity.TimeLogEntity
import com.projectpulse.android.domain.model.ProjectStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SeedData @Inject constructor(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao,
    private val timeLogDao: TimeLogDao
) {
    suspend fun seedIfEmpty() {
        if (projectDao.count().first() > 0) return

        val projectIds = sampleProjects.map { projectDao.insert(it).toInt() }

        projectIds.forEachIndexed { index, projectId ->
            val tasks = sampleTasks[index].map { it.copy(projectId = projectId) }
            tasks.forEach { taskDao.insert(it) }

            val logs = sampleTimeLogs[index].map { it.copy(projectId = projectId) }
            logs.forEach { timeLogDao.insert(it) }
        }
    }

    private companion object {
        val sampleProjects = listOf(
            ProjectEntity(
                name = "Project Pulse",
                description = "The app you are using right now — a project health dashboard.",
                status = ProjectStatus.ACTIVE
            ),
            ProjectEntity(
                name = "Personal API Hub",
                description = "FastAPI backend for personal data services.",
                status = ProjectStatus.ACTIVE
            ),
            ProjectEntity(
                name = "Vibe Coding Dashboard",
                description = "Textual TUI for tracking coding velocity and tasks.",
                status = ProjectStatus.ON_HOLD
            ),
            ProjectEntity(
                name = "Daily Trendscape",
                description = "Android app for daily tech trend briefings.",
                status = ProjectStatus.COMPLETED
            )
        )

        val sampleTasks = listOf(
            listOf(
                TaskEntity(projectId = 0, title = "Design dashboard UI", isCompleted = true),
                TaskEntity(projectId = 0, title = "Implement Room database", isCompleted = true),
                TaskEntity(projectId = 0, title = "Add statistics screen", isCompleted = false),
                TaskEntity(projectId = 0, title = "Write tests", isCompleted = false)
            ),
            listOf(
                TaskEntity(projectId = 0, title = "Set up FastAPI project", isCompleted = true),
                TaskEntity(projectId = 0, title = "Add authentication", isCompleted = true),
                TaskEntity(projectId = 0, title = "Deploy to server", isCompleted = false)
            ),
            listOf(
                TaskEntity(projectId = 0, title = "Prototype TUI screens", isCompleted = true),
                TaskEntity(projectId = 0, title = "Integrate SQLite", isCompleted = false),
                TaskEntity(projectId = 0, title = "Add velocity charts", isCompleted = false)
            ),
            listOf(
                TaskEntity(projectId = 0, title = "Build news feed", isCompleted = true),
                TaskEntity(projectId = 0, title = "Add Gemini Nano summary", isCompleted = true),
                TaskEntity(projectId = 0, title = "Publish debug APK", isCompleted = true)
            )
        )

        val sampleTimeLogs = listOf(
            listOf(
                TimeLogEntity(projectId = 0, hours = 4.0, note = "UI design"),
                TimeLogEntity(projectId = 0, hours = 6.0, note = "Database setup"),
                TimeLogEntity(projectId = 0, hours = 2.5, note = "Navigation")
            ),
            listOf(
                TimeLogEntity(projectId = 0, hours = 8.0, note = "API scaffolding"),
                TimeLogEntity(projectId = 0, hours = 5.0, note = "Auth flow")
            ),
            listOf(
                TimeLogEntity(projectId = 0, hours = 3.0, note = "TUI exploration")
            ),
            listOf(
                TimeLogEntity(projectId = 0, hours = 12.0, note = "Initial build"),
                TimeLogEntity(projectId = 0, hours = 7.0, note = "Polish and tests")
            )
        )
    }
}
