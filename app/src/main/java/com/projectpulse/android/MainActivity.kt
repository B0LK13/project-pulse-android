package com.projectpulse.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.projectpulse.android.ui.navigation.ProjectPulseNavigation
import com.projectpulse.android.ui.theme.ProjectPulseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            ProjectPulseTheme {
                val navController = rememberNavController()
                ProjectPulseNavigation(navController = navController)
            }
        }
    }
}
