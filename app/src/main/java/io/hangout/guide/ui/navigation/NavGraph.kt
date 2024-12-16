package io.hangout.guide.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseUser
import io.hangout.guide.ui.login.LoginScreen
import io.hangout.guide.ui.login.RegisterScreen
import io.hangout.guide.ui.login.ForgotPasswordScreen
import io.hangout.guide.ui.home.HomePageScreen
import io.hangout.guide.utils.AuthManager

@Composable
fun NavGraph(navController: NavHostController) {
    val authManager: AuthManager = AuthManager()

    val user: FirebaseUser? = authManager.getCurrentUser()

    NavHost(
        navController = navController,
        startDestination = if(user== null) Screen.Login.route else Screen.Home.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onNavigateToHome = { navController.navigate(Screen.Home.route)},
                auth = authManager
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateLogin = { navController.navigate(Screen.Login.route)},
                auth = authManager
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateLogin = { navController.navigate(Screen.Login.route)},
                auth = authManager
            )
        }
        composable (Screen.Home.route) {
            HomePageScreen(
                auth = authManager,
                onNavigateLogin = { navController.navigate(Screen.Login.route)}
            )
        }
    }
}