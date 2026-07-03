package com.projectpulse.android.domain.model

data class Task(
    val id: Int = 0,
    val projectId: Int,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
