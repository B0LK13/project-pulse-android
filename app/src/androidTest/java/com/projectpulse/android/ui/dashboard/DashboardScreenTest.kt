package com.projectpulse.android.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.projectpulse.android.ComposeTestBase
import com.projectpulse.android.domain.model.ProjectStatus
import org.junit.Test

class DashboardScreenTest : ComposeTestBase() {

    @Test
    fun dashboardDisplaysOverviewStats() {
        seedProject(
            name = "Dashboard Alpha",
            description = "A project for dashboard tests",
            status = ProjectStatus.ACTIVE
        )
        seedTask(projectId = 1, title = "Sample task", isCompleted = false)

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Overview").assertIsDisplayed()
        composeTestRule.onNodeWithText("Projects").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tasks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hours").assertIsDisplayed()
        composeTestRule.onNodeWithText("Completion").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dashboard Alpha").assertIsDisplayed()
    }

    @Test
    fun dashboardNavigatesToProjects() {
        composeTestRule.onNodeWithContentDescription("All projects").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Projects").assertIsDisplayed()
    }
}
