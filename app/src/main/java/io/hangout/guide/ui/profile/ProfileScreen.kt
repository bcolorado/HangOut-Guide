package io.hangout.guide.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import io.hangout.guide.R
import io.hangout.guide.data.model.UserPreferences
import io.hangout.guide.ui.common.HomeBottomBar
import io.hangout.guide.ui.navigation.topLevelDestinations
import io.hangout.guide.ui.theme.HangOutGuideTheme
import io.hangout.guide.utils.AuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(auth: AuthManager, onNavigateLogin: () -> Unit, navController: NavController, profileViewModel: ProfileViewModel) {
    val onLogoutConfirmed: () -> Unit = {
        auth.signOut()
        onNavigateLogin()
    }
    val userProfileState by profileViewModel.userProfile.collectAsState()
    var museumsPreference by remember { mutableStateOf(userProfileState?.preferences?.museums ?: false) }
    var restaurantsPreference by remember { mutableStateOf(userProfileState?.preferences?.restaurants ?: false) }
    var parksPreference by remember { mutableStateOf(userProfileState?.preferences?.parks ?: false) }
    var barsPreference by remember { mutableStateOf(userProfileState?.preferences?.bars ?: false) }

    LaunchedEffect(userProfileState) {
        if (userProfileState == null)
            profileViewModel.loadUserProfile()
    }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Hangout") },
            actions = {
                IconButton(onClick = onLogoutConfirmed, content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.logout),
                        contentDescription = "Logout",
                        tint = HangOutGuideTheme.colorScheme.onSurface
                    )
                })
            },
        )
    }, bottomBar = {
        HomeBottomBar(destinations = topLevelDestinations,
            currentDestination = navController.currentBackStackEntryAsState().value?.destination,
            onNavigateToDestination = {
                navController.navigate(it) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    restoreState = true
                    launchSingleTop = true
                }
            })
    }) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                // Profile Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.Center),
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Profile Info
                    Text(
                        text = userProfileState?.name ?: "Nombre no definido",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userProfileState?.email ?: "Email no definido",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    // Preferences Section
                    Text(
                        text = "Preferencias",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    // Preference Switches
                    PreferenceSwitch(
                        text = "Museos",
                        checked = museumsPreference,
                        onCheckedChange = {
                            museumsPreference = it
                            profileViewModel.updateUserPreferences(
                                UserPreferences(
                                    museums = museumsPreference,
                                )
                            )
                        }
                    )
                    PreferenceSwitch(
                        text = "Restaurantes",
                        checked = restaurantsPreference,
                        onCheckedChange = {
                            restaurantsPreference = it
                            profileViewModel.updateUserPreferences(
                                UserPreferences(
                                    restaurants = restaurantsPreference,
                                )
                            )
                        }
                    )
                    PreferenceSwitch(
                        text = "Parques",
                        checked = parksPreference,
                        onCheckedChange = {
                            parksPreference = it
                            profileViewModel.updateUserPreferences(
                                UserPreferences(
                                    parks = parksPreference,
                                )
                            )
                        }
                    )
                    PreferenceSwitch(
                        text = "Bares",
                        checked = barsPreference,
                        onCheckedChange = {
                            barsPreference = it
                            profileViewModel.updateUserPreferences(
                                UserPreferences(
                                    bars = barsPreference,
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun PreferenceSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF3B82F6),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.Gray
            )
        )
    }
}