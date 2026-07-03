package com.projectpulse.android.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Projects : Screen("projects")
    data object Stats : Screen("stats")
    data object ProjectDetail : Screen("projectDetail/{projectId}") {
        fun createRoute(projectId: Int) = "projectDetail/$projectId"
    }
}
