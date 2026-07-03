package com.projectpulse.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.projectpulse.android.domain.model.ProjectStatus

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val status: ProjectStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
