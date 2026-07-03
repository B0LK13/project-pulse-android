package com.projectpulse.android

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.projectpulse.android.domain.model.Project
import com.projectpulse.android.domain.model.ProjectStatus
import com.projectpulse.android.domain.model.Task
import com.projectpulse.android.domain.repository.ProjectRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

@HiltAndroidTest
abstract class ComposeTestBase {

    @get:Rule(order = 0)
    var hiltRule: HiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var repository: ProjectRepository

    @Before
    fun injectAndClear() {
        hiltRule.inject()
        runBlocking {
            clearAllData()
        }
    }

    protected fun seedProject(
        name: String,
        description: String = "",
        status: ProjectStatus = ProjectStatus.ACTIVE
    ): Project = runBlocking {
        repository.saveProject(
            Project(name = name, description = description, status = status)
        )
        repository.getAllProjects().first().first { it.name == name }
    }

    protected fun seedTask(projectId: Int, title: String, isCompleted: Boolean = false) = runBlocking {
        repository.saveTask(Task(projectId = projectId, title = title, isCompleted = isCompleted))
    }

    private suspend fun clearAllData() {
        repository.getAllProjects().first().forEach { project ->
            repository.deleteProject(project)
        }
    }
}
