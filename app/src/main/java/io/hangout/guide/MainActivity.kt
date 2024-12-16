package io.hangout.guide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.hangout.guide.ui.theme.HangOutGuideTheme
import androidx.navigation.compose.rememberNavController
import io.hangout.guide.ui.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HangOutGuideTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}