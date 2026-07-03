package com.projectpulse.android

import android.app.Application
import com.projectpulse.android.data.local.SeedData
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ProjectPulseApp : Application() {

    @Inject
    lateinit var seedData: SeedData

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            seedData.seedIfEmpty()
        }
    }
}
