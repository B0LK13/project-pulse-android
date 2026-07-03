package com.projectpulse.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectpulse.android.domain.model.ProjectWithStats
import com.projectpulse.android.domain.repository.ProjectRepository
import com.projectpulse.android.domain.usecase.DashboardStats
import com.projectpulse.android.domain.usecase.GetDashboardStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getDashboardStatsUseCase: GetDashboardStatsUseCase,
    repository: ProjectRepository
) : ViewModel() {

    val stats: StateFlow<DashboardStats> = getDashboardStatsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DashboardStats(
                totalProjects = 0,
                activeProjects = 0,
                completedProjects = 0,
                totalTasks = 0,
                remainingTasks = 0,
                totalHours = 0.0,
                averageCompletion = 0
            )
        )

    val recentProjects: StateFlow<List<ProjectWithStats>> = repository.getAllProjectsWithStats()
        .map { it.take(3) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}
