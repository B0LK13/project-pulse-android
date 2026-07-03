package com.projectpulse.android.domain.model

data class TimeLog(
    val id: Int = 0,
    val projectId: Int,
    val hours: Double,
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
)
