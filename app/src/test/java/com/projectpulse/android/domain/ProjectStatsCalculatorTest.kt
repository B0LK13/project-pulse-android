package com.projectpulse.android.domain

import com.projectpulse.android.domain.model.Task
import com.projectpulse.android.domain.usecase.ProjectStatsCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectStatsCalculatorTest {

    @Test
    fun `completionRate returns 0 for empty task list`() {
        assertEquals(0, ProjectStatsCalculator.completionRate(emptyList()))
    }

    @Test
    fun `completionRate returns 100 when all tasks completed`() {
        val tasks = listOf(
            Task(projectId = 1, title = "A", isCompleted = true),
            Task(projectId = 1, title = "B", isCompleted = true)
        )
        assertEquals(100, ProjectStatsCalculator.completionRate(tasks))
    }

    @Test
    fun `completionRate rounds correctly for partial completion`() {
        val tasks = listOf(
            Task(projectId = 1, title = "A", isCompleted = true),
            Task(projectId = 1, title = "B", isCompleted = true),
            Task(projectId = 1, title = "C", isCompleted = false)
        )
        assertEquals(66, ProjectStatsCalculator.completionRate(tasks))
    }

    @Test
    fun `averageCompletion returns 0 for empty list`() {
        assertEquals(0, ProjectStatsCalculator.averageCompletion(emptyList()))
    }

    @Test
    fun `averageCompletion calculates mean`() {
        assertEquals(50, ProjectStatsCalculator.averageCompletion(listOf(0, 100)))
    }
}
