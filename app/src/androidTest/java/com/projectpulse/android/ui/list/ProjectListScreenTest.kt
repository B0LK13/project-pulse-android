package com.projectpulse.android.ui.list

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.projectpulse.android.ComposeTestBase
import org.junit.Test

class ProjectListScreenTest : ComposeTestBase() {

    @Test
    fun addProjectAppearsInList() {
        navigateToProjects()

        composeTestRule.onNodeWithContentDescription("Add project").performClick()
        composeTestRule.onNodeWithText("Add Project").assertIsDisplayed()

        composeTestRule.onNodeWithText("Name").performTextInput("New Test Project")
        composeTestRule.onNodeWithText("Description").performTextInput("Created by UI test")
        composeTestRule.onNodeWithText("Save").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("New Test Project").assertIsDisplayed()
    }

    @Test
    fun deleteProjectRemovesIt() {
        seedProject(name = "Delete Me", description = "To be deleted")
        navigateToProjects()

        composeTestRule.onNodeWithText("Delete Me").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Delete").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithText("Delete Me").assertCountEquals(0)
    }

    private fun navigateToProjects() {
        composeTestRule.onNodeWithContentDescription("All projects").performClick()
        composeTestRule.waitForIdle()
    }
}
