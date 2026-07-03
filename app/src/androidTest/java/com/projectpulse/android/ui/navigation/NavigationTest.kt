package com.projectpulse.android.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.projectpulse.android.ComposeTestBase
import org.junit.Test

class NavigationTest : ComposeTestBase() {

    @Test
    fun dashboardToProjectsToDetailAndBack() {
        seedProject(name = "Nav Project", description = "Navigation test")

        composeTestRule.onNodeWithText("Overview").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("All projects").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Projects").assertIsDisplayed()

        composeTestRule.onNodeWithText("Nav Project").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Nav Project").assertIsDisplayed()
        composeTestRule.onNodeWithText("Project Details").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Projects").assertIsDisplayed()
    }
}
