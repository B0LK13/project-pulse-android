package com.projectpulse.android.domain.repository

import com.projectpulse.android.domain.model.Project
import com.projectpulse.android.domain.model.ProjectWithStats
import com.projectpulse.android.domain.model.Task
import com.projectpulse.android.domain.model.TimeLog
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    fun getAllProjectsWithStats(): Flow<List<ProjectWithStats>>
    suspend fun getProjectWithStatsById(id: Int): ProjectWithStats?
    suspend fun saveProject(project: Project)
    suspend fun deleteProject(project: Project)

    fun getTasksForProject(projectId: Int): Flow<List<Task>>
    suspend fun saveTask(task: Task)
    suspend fun deleteTask(task: Task)

    fun getTimeLogsForProject(projectId: Int): Flow<List<TimeLog>>
    suspend fun logTime(timeLog: TimeLog)
    suspend fun deleteTimeLog(timeLog: TimeLog)

    fun getTotalTaskCount(): Flow<Int>
    fun getRemainingTaskCount(): Flow<Int>
    fun getTotalProjectCount(): Flow<Int>
    fun getCompletedProjectCount(): Flow<Int>
    fun getActiveProjectCount(): Flow<Int>
    fun getTotalHours(): Flow<Double>
}
