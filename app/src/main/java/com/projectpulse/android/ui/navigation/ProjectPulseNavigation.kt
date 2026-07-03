package com.projectpulse.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.projectpulse.android.ui.dashboard.DashboardScreen
import com.projectpulse.android.ui.detail.ProjectDetailScreen
import com.projectpulse.android.ui.list.ProjectListScreen
import com.projectpulse.android.ui.stats.StatsScreen

@Composable
fun ProjectPulseNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) },
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                }
            )
        }
        composable(Screen.Projects.route) {
            ProjectListScreen(
                onNavigateUp = { navController.navigateUp() },
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                }
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen(onNavigateUp = { navController.navigateUp() })
        }
        composable(Screen.ProjectDetail.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull() ?: 0
            ProjectDetailScreen(
                projectId = projectId,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
