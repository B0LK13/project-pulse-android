package com.projectpulse.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.projectpulse.android.data.local.dao.ProjectDao
import com.projectpulse.android.data.local.dao.TaskDao
import com.projectpulse.android.data.local.dao.TimeLogDao
import com.projectpulse.android.data.local.entity.ProjectEntity
import com.projectpulse.android.data.local.entity.TaskEntity
import com.projectpulse.android.data.local.entity.TimeLogEntity
import com.projectpulse.android.domain.model.ProjectStatus

private val ALL_MIGRATIONS = arrayOf(MIGRATION_1_2)

@Database(
    entities = [ProjectEntity::class, TaskEntity::class, TimeLogEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(ProjectStatusConverter::class)
abstract class ProjectPulseDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao
    abstract fun timeLogDao(): TimeLogDao

    companion object {
        private const val DATABASE_NAME = "project_pulse.db"

        @Volatile
        private var INSTANCE: ProjectPulseDatabase? = null

        fun getInstance(context: Context): ProjectPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ProjectPulseDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(*ALL_MIGRATIONS)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

class ProjectStatusConverter {
    @TypeConverter
    fun fromStatus(status: ProjectStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): ProjectStatus = ProjectStatus.valueOf(value)
}
