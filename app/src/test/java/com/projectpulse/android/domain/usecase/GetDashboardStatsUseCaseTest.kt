package com.projectpulse.android.domain.usecase

import com.projectpulse.android.domain.FakeProjectRepository
import com.projectpulse.android.domain.model.Project
import com.projectpulse.android.domain.model.ProjectStatus
import com.projectpulse.android.domain.model.Task
import com.projectpulse.android.domain.model.TimeLog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDashboardStatsUseCaseTest {

    private val repository = FakeProjectRepository()
    private val useCase = GetDashboardStatsUseCase(repository)

    @Test
    fun `empty repository emits zero stats`() = runTest {
        val stats = useCase().first()
        assertEquals(0, stats.totalProjects)
        assertEquals(0, stats.activeProjects)
        assertEquals(0, stats.completedProjects)
        assertEquals(0, stats.totalTasks)
        assertEquals(0, stats.remainingTasks)
        assertEquals(0.0, stats.totalHours, 0.001)
        assertEquals(0, stats.averageCompletion)
    }

    @Test
    fun `stats reflect projects tasks and hours`() = runTest {
        repository.saveProject(Project(name = "A", description = "", status = ProjectStatus.ACTIVE))
        repository.saveProject(Project(name = "B", description = "", status = ProjectStatus.COMPLETED))

        val projectA = repository.getAllProjects().first().first { it.name == "A" }
        val projectB = repository.getAllProjects().first().first { it.name == "B" }

        repository.saveTask(Task(projectId = projectA.id, title = "T1", isCompleted = true))
        repository.saveTask(Task(projectId = projectA.id, title = "T2", isCompleted = false))
        repository.saveTask(Task(projectId = projectB.id, title = "T3", isCompleted = true))

        repository.logTime(TimeLog(projectId = projectA.id, hours = 3.5))
        repository.logTime(TimeLog(projectId = projectB.id, hours = 2.0))

        val stats = useCase().first()

        assertEquals(2, stats.totalProjects)
        assertEquals(1, stats.activeProjects)
        assertEquals(1, stats.completedProjects)
        assertEquals(3, stats.totalTasks)
        assertEquals(1, stats.remainingTasks)
        assertEquals(5.5, stats.totalHours, 0.001)
        assertEquals(75, stats.averageCompletion)
    }

    @Test
    fun `average completion is zero when no tasks exist`() = runTest {
        repository.saveProject(Project(name = "Empty", description = "", status = ProjectStatus.ACTIVE))
        val stats = useCase().first()
        assertEquals(0, stats.averageCompletion)
    }
}
