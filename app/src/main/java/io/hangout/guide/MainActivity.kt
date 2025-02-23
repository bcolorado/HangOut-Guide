package io.hangout.guide

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.hangout.guide.ui.theme.HangOutGuideTheme
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint
import io.hangout.guide.ui.chat.ChatViewModel
import io.hangout.guide.ui.home.MapViewModel
import io.hangout.guide.ui.navigation.NavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HangOutGuideTheme {
                val apiKey = stringResource(id = R.string.google_maps_access_token)
                if (!Places.isInitialized()) {
                    Places.initialize(applicationContext, apiKey)
                }
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}