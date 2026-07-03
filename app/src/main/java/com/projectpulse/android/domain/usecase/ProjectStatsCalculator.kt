package com.projectpulse.android.domain.usecase

import com.projectpulse.android.domain.model.Task

object ProjectStatsCalculator {

    fun completionRate(tasks: List<Task>): Int {
        if (tasks.isEmpty()) return 0
        return ((tasks.count { it.isCompleted }.toDouble() / tasks.size) * 100).toInt()
    }

    fun averageCompletion(rates: List<Int>): Int {
        if (rates.isEmpty()) return 0
        return rates.sum() / rates.size
    }
}
