package com.projectpulse.android.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.projectpulse.android.data.local.entity.TimeLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeLogDao {
    @Query("SELECT * FROM time_logs WHERE projectId = :projectId ORDER BY date DESC")
    fun getByProjectId(projectId: Int): Flow<List<TimeLogEntity>>

    @Query("SELECT COALESCE(SUM(hours), 0.0) FROM time_logs WHERE projectId = :projectId")
    fun totalHoursForProject(projectId: Int): Flow<Double>

    @Query("SELECT COALESCE(SUM(hours), 0.0) FROM time_logs")
    fun totalHours(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timeLog: TimeLogEntity): Long

    @Delete
    suspend fun delete(timeLog: TimeLogEntity)
}
