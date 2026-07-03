package com.projectpulse.android.domain.model

data class Project(
    val id: Int = 0,
    val name: String,
    val description: String,
    val status: ProjectStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
