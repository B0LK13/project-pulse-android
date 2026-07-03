package com.projectpulse.android.domain.usecase

import com.projectpulse.android.domain.model.ProjectStatus
import com.projectpulse.android.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class DashboardStats(
    val totalProjects: Int,
    val activeProjects: Int,
    val completedProjects: Int,
    val totalTasks: Int,
    val remainingTasks: Int,
    val totalHours: Double,
    val averageCompletion: Int
)

class GetDashboardStatsUseCase @Inject constructor(
    private val repository: ProjectRepository
) {
    operator fun invoke(): Flow<DashboardStats> = repository.getAllProjectsWithStats().map { projects ->
        val totalProjects = projects.size
        val activeProjects = projects.count { it.project.status == ProjectStatus.ACTIVE }
        val completedProjects = projects.count { it.project.status == ProjectStatus.COMPLETED }
        val totalTasks = projects.sumOf { it.totalTasks }
        val remainingTasks = projects.sumOf { it.remainingTasks }
        val totalHours = projects.sumOf { it.totalHours }
        val averageCompletion = ProjectStatsCalculator.averageCompletion(
            projects.map { it.completionRate }
        )
        DashboardStats(
            totalProjects = totalProjects,
            activeProjects = activeProjects,
            completedProjects = completedProjects,
            totalTasks = totalTasks,
            remainingTasks = remainingTasks,
            totalHours = totalHours,
            averageCompletion = averageCompletion
        )
    }
}
