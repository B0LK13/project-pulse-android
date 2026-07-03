package com.projectpulse.android.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projectpulse.android.domain.model.Project
import com.projectpulse.android.domain.model.ProjectStatus
import com.projectpulse.android.domain.model.ProjectWithStats
import com.projectpulse.android.domain.model.Task
import com.projectpulse.android.domain.model.TimeLog
import com.projectpulse.android.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ProjectRepository
) : ViewModel() {

    private val projectId: Int = when (val value = savedStateHandle.get<Any>("projectId")) {
        is Int -> value
        is String -> value.toIntOrNull() ?: 0
        else -> 0
    }

    val project: StateFlow<ProjectWithStats?> = repository.getAllProjectsWithStats()
        .map { list -> list.find { it.project.id == projectId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    fun updateProject(name: String, description: String, status: ProjectStatus) {
        viewModelScope.launch {
            val current = project.value?.project ?: return@launch
            repository.saveProject(
                current.copy(
                    name = name,
                    description = description,
                    status = status,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            repository.saveTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            repository.saveTask(
                Task(projectId = projectId, title = title)
            )
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun logHours(hours: Double, note: String = "") {
        viewModelScope.launch {
            repository.logTime(
                TimeLog(projectId = projectId, hours = hours, note = note)
            )
        }
    }
}
