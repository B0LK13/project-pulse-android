package com.projectpulse.android.domain

import com.projectpulse.android.domain.model.Project
import com.projectpulse.android.domain.model.ProjectStatus
import com.projectpulse.android.domain.model.ProjectWithStats
import com.projectpulse.android.domain.model.Task
import com.projectpulse.android.domain.model.TimeLog
import com.projectpulse.android.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FakeProjectRepository : ProjectRepository {

    private val projectsFlow = MutableStateFlow<List<Project>>(emptyList())
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    private val timeLogsFlow = MutableStateFlow<List<TimeLog>>(emptyList())

    override fun getAllProjects(): Flow<List<Project>> = projectsFlow

    override fun getAllProjectsWithStats(): Flow<List<ProjectWithStats>> {
        return combine(projectsFlow, tasksFlow, timeLogsFlow) { projects, tasks, logs ->
            projects.map { project ->
                val projectTasks = tasks.filter { it.projectId == project.id }
                val projectLogs = logs.filter { it.projectId == project.id }
                val completed = projectTasks.count { it.isCompleted }
                val completion = if (projectTasks.isEmpty()) 0 else (completed * 100 / projectTasks.size)
                ProjectWithStats(
                    project = project,
                    tasks = projectTasks,
                    totalHours = projectLogs.sumOf { it.hours },
                    completionRate = completion
                )
            }
        }
    }

    override suspend fun getProjectWithStatsById(id: Int): ProjectWithStats? {
        return getAllProjectsWithStats().map { list -> list.find { it.project.id == id } }
            .let { null }
    }

    override suspend fun saveProject(project: Project) {
        val current = projectsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            current[index] = project
        } else {
            val nextId = (current.maxOfOrNull { it.id } ?: 0) + 1
            current.add(project.copy(id = nextId))
        }
        projectsFlow.value = current
    }

    override suspend fun deleteProject(project: Project) {
        projectsFlow.value = projectsFlow.value.filter { it.id != project.id }
    }

    override fun getTasksForProject(projectId: Int): Flow<List<Task>> =
        tasksFlow.map { tasks -> tasks.filter { it.projectId == projectId } }

    override suspend fun saveTask(task: Task) {
        val current = tasksFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == task.id }
        if (index >= 0) {
            current[index] = task
        } else {
            val nextId = (current.maxOfOrNull { it.id } ?: 0) + 1
            current.add(task.copy(id = nextId))
        }
        tasksFlow.value = current
    }

    override suspend fun deleteTask(task: Task) {
        tasksFlow.value = tasksFlow.value.filter { it.id != task.id }
    }

    override fun getTimeLogsForProject(projectId: Int): Flow<List<TimeLog>> =
        timeLogsFlow.map { logs -> logs.filter { it.projectId == projectId } }

    override suspend fun logTime(timeLog: TimeLog) {
        val current = timeLogsFlow.value.toMutableList()
        val nextId = (current.maxOfOrNull { it.id } ?: 0) + 1
        current.add(timeLog.copy(id = nextId))
        timeLogsFlow.value = current
    }

    override suspend fun deleteTimeLog(timeLog: TimeLog) {
        timeLogsFlow.value = timeLogsFlow.value.filter { it.id != timeLog.id }
    }

    override fun getTotalTaskCount(): Flow<Int> = tasksFlow.map { it.size }
    override fun getRemainingTaskCount(): Flow<Int> = tasksFlow.map { it.count { !it.isCompleted } }
    override fun getTotalProjectCount(): Flow<Int> = projectsFlow.map { it.size }
    override fun getCompletedProjectCount(): Flow<Int> = projectsFlow.map { it.count { it.status == ProjectStatus.COMPLETED } }
    override fun getActiveProjectCount(): Flow<Int> = projectsFlow.map { it.count { it.status == ProjectStatus.ACTIVE } }
    override fun getTotalHours(): Flow<Double> = timeLogsFlow.map { logs -> logs.sumOf { it.hours } }
}
