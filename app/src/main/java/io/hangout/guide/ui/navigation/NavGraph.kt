package io.hangout.guide.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.hangout.guide.ui.login.LoginScreen
import io.hangout.guide.ui.login.RegisterScreen
import io.hangout.guide.ui.login.ForgotPasswordScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onNavigateToHome = { navController.navigate(Screen.Home.route)}
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateLogin = { navController.navigate(Screen.Login.route)}
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateLogin = { navController.navigate(Screen.Login.route)}
            )
        }
        composable (Screen.Home.route) {
            // Implementar home
        }
    }
}