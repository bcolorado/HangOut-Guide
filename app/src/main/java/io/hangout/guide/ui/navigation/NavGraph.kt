package io.hangout.guide.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseUser
import io.hangout.guide.ui.chat.ChatScreen
import io.hangout.guide.ui.home.HomePageScreen
import io.hangout.guide.ui.login.ForgotPasswordScreen
import io.hangout.guide.ui.login.LoginScreen
import io.hangout.guide.ui.login.RegisterScreen
import io.hangout.guide.ui.place.PlaceScreen
import io.hangout.guide.ui.profile.ProfileScreen
import io.hangout.guide.ui.profile.ProfileViewModel
import io.hangout.guide.ui.theme.HangOutGuideTheme
import io.hangout.guide.utils.AuthManager

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun NavGraph(navController: NavHostController) {
    val authManager = AuthManager()

    val user: FirebaseUser? by remember { mutableStateOf(authManager.getCurrentUser()) }

    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val isLoading by profileViewModel.isLoading.collectAsState()
    LaunchedEffect(user) {
        if (user != null) {
            profileViewModel.loadUserProfile()
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(HangOutGuideTheme.colorScheme.surface)) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if(user== null) Screen.Login else Screen.Home
    ) {
        composable<Screen.Login> {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register) },
                onNavigateForgotPassword = { navController.navigate(Screen.ForgotPassword) },
                onNavigateToHome = { navController.navigate(Screen.Home)},
                auth = authManager
            )
        }
        composable<Screen.Register> {
            RegisterScreen(
                onNavigateLogin = { navController.navigate(Screen.Login)},
                auth = authManager,
                profileViewModel = profileViewModel
            )
        }
        composable<Screen.ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateLogin = { navController.navigate(Screen.Login)},
                auth = authManager
            )
        }
        composable<Screen.Home> {
            HomePageScreen(
                auth = authManager,
                onNavigateLogin = { navController.navigate(Screen.Login)},
                onSeeRecommendedPlaces = { place ->
                    navController.popBackStack()
                    navController.navigate(Screen.Chat(
                        search = true,
                        featureName = place?.featureName,
                        country = place?.country,
                        address = place?.address,
                        postalCode = place?.postalCode,
                        longitude = place?.longitude,
                        latitude = place?.latitude
                    ))
                },
                navController = navController
            )
        }
        composable<Screen.Chat> {
            val args = it.toRoute<Screen.Chat>()
            val place = PlaceAddress(
                search = args.search,
                featureName = args.featureName,
                country = args.country,
                address = args.address,
                postalCode = args.postalCode,
                longitude = args.longitude,
                latitude = args.latitude
            )
            ChatScreen(
                auth = authManager,
                onNavigateLogin = { navController.navigate(Screen.Login)},
                navController = navController,
                place = place,
                profile = profileViewModel.userProfile.value,
            )
        }
        composable<Screen.Profile> {
            ProfileScreen(
                auth = authManager,
                onNavigateLogin = { navController.navigate(Screen.Login)},
                navController = navController,
                profileViewModel = profileViewModel
            )
        }
        composable<Screen.Place> {
            PlaceScreen()
        }
    }
}
