package com.projectpulse.android.domain.model

data class ProjectWithStats(
    val project: Project,
    val tasks: List<Task>,
    val totalHours: Double,
    val completionRate: Int
) {
    val remainingTasks: Int = tasks.count { !it.isCompleted }
    val completedTasks: Int = tasks.count { it.isCompleted }
    val totalTasks: Int = tasks.size
}
