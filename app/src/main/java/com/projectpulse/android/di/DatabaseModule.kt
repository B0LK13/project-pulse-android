package com.projectpulse.android.di

import android.content.Context
import androidx.room.Room
import com.projectpulse.android.data.local.ProjectPulseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ProjectPulseDatabase {
        return Room.databaseBuilder(
            context,
            ProjectPulseDatabase::class.java,
            "project_pulse.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideProjectDao(database: ProjectPulseDatabase) = database.projectDao()

    @Provides
    fun provideTaskDao(database: ProjectPulseDatabase) = database.taskDao()

    @Provides
    fun provideTimeLogDao(database: ProjectPulseDatabase) = database.timeLogDao()
}
