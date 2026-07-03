package com.projectpulse.android.data.repository

import com.projectpulse.android.data.local.dao.ProjectDao
import com.projectpulse.android.data.local.dao.TaskDao
import com.projectpulse.android.data.local.dao.TimeLogDao
import com.projectpulse.android.data.local.entity.ProjectEntity
import com.projectpulse.android.data.local.entity.TaskEntity
import com.projectpulse.android.data.local.entity.TimeLogEntity
import com.projectpulse.android.domain.model.Project
import com.projectpulse.android.domain.model.ProjectStatus
import com.projectpulse.android.domain.model.ProjectWithStats
import com.projectpulse.android.domain.model.Task
import com.projectpulse.android.domain.model.TimeLog
import com.projectpulse.android.domain.repository.ProjectRepository
import com.projectpulse.android.domain.usecase.ProjectStatsCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao,
    private val timeLogDao: TimeLogDao
) : ProjectRepository {

    override fun getAllProjects(): Flow<List<Project>> = projectDao.getAll()
        .map { entities -> entities.map { it.toDomain() } }

    override fun getAllProjectsWithStats(): Flow<List<ProjectWithStats>> =
        projectDao.getAll().flatMapLatest { projects ->
            if (projects.isEmpty()) {
                flowOf(emptyList())
            } else {
                val projectFlows = projects.map { project ->
                    combine(
                        taskDao.getByProjectId(project.id),
                        timeLogDao.totalHoursForProject(project.id)
                    ) { tasks, hours ->
                        val domainTasks = tasks.map { it.toDomain() }
                        ProjectWithStats(
                            project = project.toDomain(),
                            tasks = domainTasks,
                            totalHours = hours,
                            completionRate = ProjectStatsCalculator.completionRate(domainTasks)
                        )
                    }
                }
                combine(projectFlows) { it.toList() }
            }
        }

    override suspend fun getProjectWithStatsById(id: Int): ProjectWithStats? {
        val project = projectDao.getById(id) ?: return null
        val tasks = taskDao.getByProjectId(id).first().map { it.toDomain() }
        val hours = timeLogDao.totalHoursForProject(id).first()
        return ProjectWithStats(
            project = project.toDomain(),
            tasks = tasks,
            totalHours = hours,
            completionRate = ProjectStatsCalculator.completionRate(tasks)
        )
    }

    override suspend fun saveProject(project: Project) {
        if (project.id == 0) {
            projectDao.insert(project.toEntity())
        } else {
            projectDao.update(project.toEntity())
        }
    }

    override suspend fun deleteProject(project: Project) {
        projectDao.delete(project.toEntity())
    }

    override fun getTasksForProject(projectId: Int): Flow<List<Task>> =
        taskDao.getByProjectId(projectId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveTask(task: Task) {
        if (task.id == 0) {
            taskDao.insert(task.toEntity())
        } else {
            taskDao.update(task.toEntity())
        }
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toEntity())
    }

    override fun getTimeLogsForProject(projectId: Int): Flow<List<TimeLog>> =
        timeLogDao.getByProjectId(projectId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun logTime(timeLog: TimeLog) {
        timeLogDao.insert(timeLog.toEntity())
    }

    override suspend fun deleteTimeLog(timeLog: TimeLog) {
        timeLogDao.delete(timeLog.toEntity())
    }

    override fun getTotalTaskCount(): Flow<Int> = taskDao.count()
    override fun getRemainingTaskCount(): Flow<Int> = taskDao.remainingCount()
    override fun getTotalProjectCount(): Flow<Int> = projectDao.count()
    override fun getCompletedProjectCount(): Flow<Int> = projectDao.completedCount()
    override fun getActiveProjectCount(): Flow<Int> = projectDao.activeCount()
    override fun getTotalHours(): Flow<Double> = timeLogDao.totalHours()
}

private fun ProjectEntity.toDomain(): Project = Project(
    id = id,
    name = name,
    description = description,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun Project.toEntity(): ProjectEntity = ProjectEntity(
    id = id,
    name = name,
    description = description,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun TaskEntity.toDomain(): Task = Task(
    id = id,
    projectId = projectId,
    title = title,
    isCompleted = isCompleted,
    createdAt = createdAt
)

private fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    projectId = projectId,
    title = title,
    isCompleted = isCompleted,
    createdAt = createdAt
)

private fun TimeLogEntity.toDomain(): TimeLog = TimeLog(
    id = id,
    projectId = projectId,
    hours = hours,
    date = date,
    note = note
)

private fun TimeLog.toEntity(): TimeLogEntity = TimeLogEntity(
    id = id,
    projectId = projectId,
    hours = hours,
    date = date,
    note = note
)
