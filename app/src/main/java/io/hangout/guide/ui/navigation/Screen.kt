package io.hangout.guide.ui.navigation

import io.hangout.guide.R
import io.hangout.guide.ui.common.TopLevelDestination
import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Login : Screen()

    @Serializable
    data object Register : Screen()

    @Serializable
    data object ForgotPassword : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data class Chat(
        val search: Boolean = false,
        val featureName: String?,
        val country: String?,
        val address: String?,
        val postalCode: String?,
        val latitude: Double?,
        val longitude: Double?,
    ) : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Place : Screen()
}

@Serializable
data class PlaceAddress(
    val search: Boolean = false,
    val address: String?,
    val country: String?,
    val featureName: String?,
    val postalCode: String?,
    val latitude: Double?,
    val longitude: Double?,
)


val topLevelDestinations = listOf(
    TopLevelDestination(
        route = Screen.Home,
        selectedIcon = R.drawable.home_24dp_e8eaed_fill1_wght400_grad0_opsz24,
        unselectedIcon = R.drawable.home_24dp_e8eaed_fill0_wght400_grad0_opsz24,
        iconText = "Inicio"
    ), TopLevelDestination(
        route = Screen.Profile,
        selectedIcon = R.drawable.person_24dp_e8eaed_fill1_wght400_grad0_opsz24,
        unselectedIcon = R.drawable.person_24dp_e8eaed_fill0_wght400_grad0_opsz24,
        iconText = "Perfil"
    ), TopLevelDestination(
        route = Screen.Chat(
            featureName = null,
            country = null,
            address = null,
            postalCode = null,
            latitude = null,
            longitude = null
        ),
        selectedIcon = R.drawable.chat_24dp_e8eaed_fill1_wght400_grad0_opsz24,
        unselectedIcon = R.drawable.chat_24dp_e8eaed_fill0_wght400_grad0_opsz24,
        iconText = "Chat"
    )
)

// Método de extensión para obtener la ruta de Screen
fun Screen.getRoute(): String = when (this) {
    is Screen.Login -> "login"
    is Screen.Register -> "register"
    is Screen.ForgotPassword -> "forgotPassword"
    is Screen.Home -> "home"
    is Screen.Chat -> "chat"
    is Screen.Profile -> "profile"
    is Screen.Place -> "place"
}
