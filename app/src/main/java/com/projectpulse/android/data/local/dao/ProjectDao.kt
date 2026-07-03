package com.projectpulse.android.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.projectpulse.android.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getById(id: Int): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: ProjectEntity): Long

    @Update
    suspend fun update(project: ProjectEntity)

    @Delete
    suspend fun delete(project: ProjectEntity)

    @Query("SELECT COUNT(*) FROM projects")
    fun count(): Flow<Int>

    @Query("SELECT COUNT(*) FROM projects WHERE status = 'COMPLETED'")
    fun completedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM projects WHERE status = 'ACTIVE'")
    fun activeCount(): Flow<Int>
}
