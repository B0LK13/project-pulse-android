package com.projectpulse.android.ui.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.projectpulse.android.ComposeTestBase
import org.junit.Test

class ProjectDetailScreenTest : ComposeTestBase() {

    @Test
    fun detailDisplaysProjectInfoAndTasks() {
        val project = seedProject(name = "Detail Project", description = "Detail test")
        seedTask(projectId = project.id, title = "Existing Task", isCompleted = false)
        navigateToProjectDetail("Detail Project")

        composeTestRule.onNodeWithText("Detail Project").assertIsDisplayed()
        composeTestRule.onNodeWithText("Existing Task").assertIsDisplayed()
    }

    @Test
    fun addTaskAppearsInList() {
        seedProject(name = "Task Project", description = "Task test")
        navigateToProjectDetail("Task Project")

        composeTestRule.onNodeWithContentDescription("Add task").performClick()
        composeTestRule.onNodeWithText("Add Task").assertIsDisplayed()
        composeTestRule.onNodeWithText("Task title").performTextInput("Brand New Task")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Brand New Task").assertIsDisplayed()
    }

    @Test
    fun toggleTaskUpdatesCompletion() {
        val project = seedProject(name = "Toggle Project", description = "Toggle test")
        seedTask(projectId = project.id, title = "Toggle Task", isCompleted = false)
        navigateToProjectDetail("Toggle Project")

        composeTestRule.onNodeWithText("Toggle Task").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Toggle Task").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Toggle Task").assertIsDisplayed()
    }

    private fun navigateToProjectDetail(projectName: String) {
        composeTestRule.onNodeWithText(projectName).performClick()
        composeTestRule.waitForIdle()
    }
}
